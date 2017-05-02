package com.inuker.bluetooth.library.channel;

/**
 * Created by dingjikerbo on 17/4/19.
 */

public interface IChannel {

	/**
	 * 底层写数据
	 */
	void write(final byte[] bytes, ChannelCallback callback);

	/**
	 * 通知底层读到数据
	 */
	void onRead(final byte[] bytes);

	/**
	 * 通知上层收到数据
	 */
	void onRecv(byte[] bytes);

	/**
	 * 上层发数据
	 */
	void send(byte[] value, ChannelCallback callback);
}
