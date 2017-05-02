package com.inuker.bluetooth.library.channel;

/**
 * Created by dingjikerbo on 17/4/14.
 */

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.inuker.bluetooth.library.BluetoothContext;
import com.inuker.bluetooth.library.channel.packet.ACKPacket;
import com.inuker.bluetooth.library.channel.packet.CTRPacket;
import com.inuker.bluetooth.library.channel.packet.DataPacket;
import com.inuker.bluetooth.library.channel.packet.Packet;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyBulk;
import com.inuker.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * 要保证所有操作都在同一个线程
 */
public abstract class Channel implements IChannel, ProxyInterceptor {

	private static final long TIMEOUT = 5000;
	private static final int MSG_WRITE_CALLBACK = 1;
	private static final String TIMER_EXCEPTION = "exception";

	private ChannelState mCurrentState = ChannelState.IDLE;

	private byte[] mBytesToWrite;

	/**
	 * 收到的包
	 */
	private SparseArray<Packet> mPacketRecv;
	private int mCurrentSync;

	/**
	 * 发端要记录总字节数
	 */
	private int mTotalBytes;

	/**
	 * 收端要记录总帧数
	 */
	private int mFrameCount;

	private ChannelCallback mChannelCallback;

	private Handler mWorkerHandler;

	private IChannel mChannel;

	private int mLastSync;

	public Channel() {
		mPacketRecv = new SparseArray<>();

		mChannel = ProxyUtils.getProxy(mChannelImpl, this);

		HandlerThread thread = new HandlerThread(getClass().getSimpleName());
		thread.start();

		mWorkerHandler = new Handler(thread.getLooper(), mCallback);
	}

	@Override
	public final void onRead(final byte[] bytes) {
		mChannel.onRead(bytes);
	}

	@Override
	public final void send(byte[] value, ChannelCallback callback) {
		BluetoothLog.e(String.format(">>> send %s", new String(value)));
		mChannel.send(value, callback);
	}

	private final IChannelStateHandler mSyncPacketHandler = new IChannelStateHandler() {

		@Override
		public void handleState(Object... args) {
			assertRuntime(false);

			DataPacket dataPacket = (DataPacket) args[0];

			if (dataPacket.getSeq() != mCurrentSync) {
				BluetoothLog.w(String.format("sync packet not matched!!"));
				return;
			}

			if (!onDataPacketRecvd(dataPacket)) {
				BluetoothLog.w(String.format("sync packet repeated!!"));
				return;
			}

			mLastSync = mCurrentSync;
			mCurrentSync = 0;

			startSyncPacket();
		}
	};

	/**
	 * 收到数据包的回调
	 */
	private final IChannelStateHandler mRecvDataHandler = new IChannelStateHandler() {

		@Override
		public void handleState(Object... args) {
			assertRuntime(false);
			DataPacket dataPacket = (DataPacket) args[0];

			if (!onDataPacketRecvd(dataPacket)) {
				BluetoothLog.w(String.format("dataPacket repeated!!"));
				return;
			}

			if (dataPacket.getSeq() == mFrameCount) {
				// 如果最后一帧收到了，说明对端发送完毕了
				startSyncPacket();
			} else {
				startTimer(TIMEOUT, new Timer.TimerCallback("WaitData") {
					@Override
					public void onTimerCallback() {
						startSyncPacket();
					}
				});
			}
		}
	};

	/**
	 * 收到流控包的回调
	 */
	private final IChannelStateHandler mRecvCTRHandler = new IChannelStateHandler() {
		@Override
		public void handleState(Object... args) {
			assertRuntime(false);

			CTRPacket ctrPacket = (CTRPacket) args[0];
			mFrameCount = ctrPacket.getFrameCount();
			ACKPacket ackPacket = new ACKPacket(ACKPacket.READY);

			setCurrentState(ChannelState.READY);

			performWrite(ackPacket, new ChannelCallback() {
				@Override
				public void onCallback(int code) {
					assertRuntime(false);
					if (code == Code.SUCCESS) {
						setCurrentState(ChannelState.READING);
						startTimer();
					} else {
						resetChannelStatus();
					}
				}
			});
		}
	};

	private final IChannelStateHandler mWaitStartACKHandler = new IChannelStateHandler() {
		@Override
		public void handleState(Object... args) {
			assertRuntime(false);
			setCurrentState(ChannelState.WAIT_START_ACK);
			startTimer();
		}
	};

	private final Timer.TimerCallback mTimeoutHandler = new Timer.TimerCallback(getClass().getSimpleName()) {

		@Override
		public void onTimerCallback() {
			assertRuntime(false);
			onSendCallback(Code.TIMEOUT);
			resetChannelStatus();
		}
	};

	/**
	 * 收到ACK包的回调
	 */
	private final IChannelStateHandler mRecvACKHandler = new IChannelStateHandler() {
		@Override
		public void handleState(Object... args) {
			assertRuntime(false);

			ACKPacket ackPacket = (ACKPacket) args[0];

			switch (ackPacket.getStatus()) {
				case ACKPacket.READY:
					stopTimer();
					setCurrentState(ChannelState.WRITING);
					sendDataPacket(0, true);
					break;

				case ACKPacket.SYNC:
					int index = ackPacket.getSeq();
					if (index >= 1 && index <= mFrameCount) {
						sendDataPacket(index - 1, false);
						startTimer();
					}
					break;

				case ACKPacket.SUCCESS:
					onSendCallback(Code.SUCCESS);
					resetChannelStatus();
					break;

				default:
					onSendCallback(Code.FAIL);
					resetChannelStatus();
			}
		}
	};

	private final ChannelStateBlock[] STATE_MACHINE = {
			new ChannelStateBlock(ChannelState.READY, ChannelEvent.SEND_CTR, mWaitStartACKHandler),
			new ChannelStateBlock(ChannelState.WAIT_START_ACK, ChannelEvent.RECV_ACK, mRecvACKHandler),
			new ChannelStateBlock(ChannelState.SYNC, ChannelEvent.RECV_ACK, mRecvACKHandler),
			new ChannelStateBlock(ChannelState.IDLE, ChannelEvent.RECV_CTR, mRecvCTRHandler),
			new ChannelStateBlock(ChannelState.READING, ChannelEvent.RECV_DATA, mRecvDataHandler),
			new ChannelStateBlock(ChannelState.SYNC_ACK, ChannelEvent.RECV_DATA, mSyncPacketHandler),
	};

	/**
	 * 这个函数主要是为了记录写出去的所有包
	 * 执行写要放在UI线程
	 */
	private void performWrite(Packet packet, final ChannelCallback callback) {
		assertRuntime(false);

		if (callback == null) {
			throw new NullPointerException("callback can't be null");
		}

		// 此处为防止底层写没回调，故抛异常提示
		if (!isTimerOn()) {
			startExceptionTimer();
		}

		final byte[] bytes = packet.toBytes();
		BluetoothLog.w(String.format("%s: %s", getLogTag(), packet));

		BluetoothContext.post(new Runnable() {

			@Override
			public void run() {
				write(bytes, new WriteCallback(callback));
			}
		});
	}

	private class WriteCallback implements ChannelCallback {

		ChannelCallback callback;

		WriteCallback(ChannelCallback callback) {
			this.callback = callback;
		}

		@Override
		public void onCallback(int code) {
			if (isExceptionTimerOn()) {
				stopTimer();
			}

			mWorkerHandler.obtainMessage(MSG_WRITE_CALLBACK, code, 0, callback).sendToTarget();
		}
	}

	private void sendStartFlowPacket() {
		assertRuntime(false);

		CTRPacket flowPacket = new CTRPacket(mFrameCount);

		performWrite(flowPacket, new ChannelCallback() {
			@Override
			public void onCallback(int code) {
				assertRuntime(false);

				if (code == Code.SUCCESS) {
					onPostState(ChannelEvent.SEND_CTR);
				} else {
					onSendCallback(Code.FAIL);
					resetChannelStatus();
				}
			}

		});
	}

	private void onSendCallback(final int code) {
		assertRuntime(false);

		BluetoothLog.v(String.format("%s: code = %d", getLogTag(), code));

		if (mChannelCallback != null) {
			mChannelCallback.onCallback(code);
		}
	}

	private boolean onDataPacketRecvd(DataPacket packet) {
		assertRuntime(false);

		// 如果对端包发重复了，则直接忽略
		if (mPacketRecv.get(packet.getSeq()) != null) {
			return false;
		}

		if (packet.getSeq() == mFrameCount) {
			packet.setLastFrame();
		}

		mPacketRecv.put(packet.getSeq(), packet);
		mTotalBytes += packet.getDataLength();
		stopTimer();

		return true;
	}

	/**
	 * 认为对端发送完毕了，可以开始同步了
	 */
	private void startSyncPacket() {
		assertRuntime(false);

		BluetoothLog.v(getLogTag());

		startTimer();
		setCurrentState(ChannelState.SYNC);

		if (!syncLostPacket()) {
			// 所有包都同步完了

			final byte[] bytes = getTotalRecvdBytes();

			if (!ByteUtils.isEmpty(bytes)) {
				ACKPacket ackPacket = new ACKPacket(ACKPacket.SUCCESS);
				performWrite(ackPacket, new ChannelCallback() {
					@Override
					public void onCallback(int code) {
						assertRuntime(false);
						resetChannelStatus();

						if (code == Code.SUCCESS) {
							dispatchOnReceive(bytes);
						}
					}
				});
			} else {
				resetChannelStatus();
			}
		} else {
			// 什么都不做
		}
	}

	private void dispatchOnReceive(final byte[] bytes) {
		BluetoothLog.e(String.format(">>> receive: %s", new String(bytes)));
		BluetoothContext.post(new RecvCallback(bytes));
	}

	private class RecvCallback implements Runnable {

		private byte[] bytes;

		RecvCallback(byte[] bytes) {
			this.bytes = bytes;
		}

		@Override
		public void run() {
			onRecv(bytes);
		}
	}

	private byte[] getTotalRecvdBytes() {
		assertRuntime(false);

		if (mPacketRecv.size() != mFrameCount) {
			throw new IllegalStateException();
		}

		BluetoothLog.v(String.format("%s: totalBytes = %d", getLogTag(), mTotalBytes));

		ByteBuffer buffer = ByteBuffer.allocate(mTotalBytes);

		for (int i = 1; i <= mFrameCount; i++) {
			DataPacket packet = (DataPacket) mPacketRecv.get(i);

			packet.fillByteBuffer(buffer);

			if (i == mFrameCount) {
				if (!checkCRC(buffer.array(), packet.getCrc())) {
					BluetoothLog.e(String.format("check crc failed!!"));
					return ByteUtils.EMPTY_BYTES;
				}
			}
		}

		return buffer.array();
	}

	private boolean syncLostPacket() {
		assertRuntime(false);

		BluetoothLog.v(getLogTag());

		int i;

		for (i = mLastSync + 1; i <= mFrameCount; i++) {
			if (mPacketRecv.get(i) == null) {
				break;
			}
		}

		if (i <= mFrameCount) {
			mCurrentSync = i;

			ACKPacket ackPacket = new ACKPacket(ACKPacket.SYNC, i);
			performWrite(ackPacket, new ChannelCallback() {
				@Override
				public void onCallback(int code) {
					assertRuntime(false);
					if (code == Code.SUCCESS) {
						setCurrentState(ChannelState.SYNC_ACK);
						startTimer();
					} else {
						resetChannelStatus();
					}
				}
			});

			return true;
		}

		return false;
	}

	private void resetChannelStatus() {
		assertRuntime(false);

		BluetoothLog.v(getLogTag());

		stopTimer();
		setCurrentState(ChannelState.IDLE);
		mBytesToWrite = null;
		mFrameCount = 0;
		mChannelCallback = null;
		mPacketRecv.clear();
		mCurrentSync = 0;
		mLastSync = 0;
		mTotalBytes = 0;
	}

	/**
	 * @param index  包的索引，从0开始
	 * @param looped 是否要循环发送下一个包
	 */
	private void sendDataPacket(final int index, final boolean looped) {
		assertRuntime(false);

		if (index >= mFrameCount) {
			BluetoothLog.v(String.format("%s: all packets sended!!", getLogTag()));
			setCurrentState(ChannelState.SYNC);
			startTimer(TIMEOUT * 3);
			return;
		}

		BluetoothLog.v(String.format("%s: index = %d, looped = %b", getLogTag(), index + 1, looped));

		int start = index * 18;
		int end = Math.min(mBytesToWrite.length, (index + 1) * 18); // 开区间

		DataPacket dataPacket = new DataPacket(index + 1, mBytesToWrite, start, end);

		performWrite(dataPacket, new ChannelCallback() {
			@Override
			public void onCallback(int code) {
				assertRuntime(false);
				if (code != Code.SUCCESS) {
					BluetoothLog.w(String.format(">>> packet %d write failed", index));
				}
				if (looped) {
					sendDataPacket(index + 1, looped);
				}
			}

		});
	}

	private void setCurrentState(ChannelState state) {
		assertRuntime(false);
		BluetoothLog.v(String.format("%s: state = %s", getLogTag(), state));
		mCurrentState = state;
	}

	private void onPostState(ChannelEvent event, Object... args) {
		assertRuntime(false);

		BluetoothLog.v(String.format("%s: state = %s, event = %s",
				getLogTag(), mCurrentState, event));

		for (ChannelStateBlock block : STATE_MACHINE) {
			if (block.state == mCurrentState && block.event == event) {
				block.handler.handleState(args);
				break;
			}
		}
	}

	private void assertRuntime(boolean sync) {
		Looper target = sync ? Looper.getMainLooper() : mWorkerHandler.getLooper();
		if (Looper.myLooper() != target) {
			throw new RuntimeException();
		}
	}

	private void performOnRead(byte[] bytes) {
		assertRuntime(false);

		Packet packet = Packet.getPacket(bytes);

		BluetoothLog.w(String.format("%s: %s", getLogTag(), packet));

		switch (packet.getName()) {
			case Packet.ACK:
				onPostState(ChannelEvent.RECV_ACK, packet);
				break;

			case Packet.DATA:
				onPostState(ChannelEvent.RECV_DATA, packet);
				break;

			case Packet.CTR:
				onPostState(ChannelEvent.RECV_CTR, packet);
				break;

			default:
				// 非法的包直接忽略
				break;
		}
	}

	private final IChannel mChannelImpl = new IChannel() {

		@Override
		public void write(byte[] bytes, ChannelCallback callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void onRead(byte[] bytes) {
			performOnRead(bytes);
		}

		@Override
		public void onRecv(byte[] bytes) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void send(byte[] value, ChannelCallback callback) {
			performSend(value, callback);
		}
	};

	private void performSend(byte[] value, ChannelCallback callback) {
		assertRuntime(false);

		if (mCurrentState != ChannelState.IDLE) {
			callback.onCallback(Code.BUSY);
			return;
		}

		mCurrentState = ChannelState.READY;
		mChannelCallback = ProxyUtils.getUIProxy(callback);

		mTotalBytes = value.length;
		mFrameCount = getFrameCount(mTotalBytes);

		BluetoothLog.v(String.format("%s: totalBytes = %d, frameCount = %d",
				getLogTag(), mTotalBytes, mFrameCount));

		mBytesToWrite = Arrays.copyOf(value, value.length + 2);
		byte[] crc = CRC16.get(value);
		System.arraycopy(crc, 0, mBytesToWrite, value.length, 2);

		sendStartFlowPacket();
	}

	@Override
	public boolean onIntercept(Object object, Method method, Object[] args) {
		mWorkerHandler.obtainMessage(0, new ProxyBulk(object, method, args)).sendToTarget();
		return true;
	}

	private final Handler.Callback mCallback = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_WRITE_CALLBACK:
					ChannelCallback callback = (ChannelCallback) msg.obj;
					callback.onCallback(msg.arg1);
					break;

				default:
					ProxyBulk.safeInvoke(msg.obj);
					break;
			}

			return false;
		}
	};

	private String getLogTag() {
		return String.format("%s.%s", getClass().getSimpleName(),
				BluetoothContext.getCurrentMethodName());
	}

	/**
	 * 末尾追加两个字节的crc，每包发18个字节
	 *
	 * @return 分包数
	 */
	private int getFrameCount(int totalBytes) {
		int total = totalBytes + 2;
		return 1 + (total - 1) / 18;
	}

	private void startTimer() {
		startTimer(TIMEOUT);
	}

	private void startExceptionTimer() {
		startTimer(TIMEOUT, new Timer.TimerCallback(TIMER_EXCEPTION) {
			@Override
			public void onTimerCallback() throws TimeoutException {
				throw new TimeoutException();
			}
		});
	}

	private void startTimer(long duration) {
		startTimer(duration, mTimeoutHandler);
	}

	private void startTimer(long duration, Timer.TimerCallback callback) {
		BluetoothLog.v(String.format("%s: duration = %d", getLogTag(), duration));
		Timer.start(callback, duration);
	}

	private void stopTimer() {
		BluetoothLog.v(getLogTag());
		Timer.stop();
	}

	private boolean isTimerOn() {
		return Timer.isRunning();
	}

	private boolean isExceptionTimerOn() {
		return TIMER_EXCEPTION.equals(Timer.getName());
	}

	private boolean checkCRC(byte[] bytes, byte[] crc0) {
		return ByteUtils.equals(crc0, CRC16.get(bytes));
	}
}
