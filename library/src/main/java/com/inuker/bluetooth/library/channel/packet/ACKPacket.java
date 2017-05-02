package com.inuker.bluetooth.library.channel.packet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

/**
 * Created by dingjikerbo on 17/4/16.
 */

public class ACKPacket extends Packet {

	/**
	 * 数据同步成功
	 */
	public static final int SUCCESS = 0;

	/**
	 * 设备就绪
	 */
	public static final int READY = 1;

	/**
	 * 设备繁忙
	 */
	public static final int BUSY = 2;

	/**
	 * 同步超时
	 */
	public static final int TIMEOUT = 3;

	/**
	 * 取消同步
	 */
	public static final int CANCEL = 4;

	/**
	 * 同步丢包
	 */
	public static final int SYNC = 5;

	private int status;

	/**
	 * 序号从1开始
	 */
	private int seq;

	public ACKPacket(int status) {
		this(status, 0);
	}

	public ACKPacket(int status, int seq) {
		this.status = status;
		this.seq = seq;
	}

	@Override
	public String getName() {
		return ACK;
	}

	public int getStatus() {
		return status;
	}

	public int getSeq() {
		return seq;
	}

	@Override
	public byte[] toBytes() {
		ByteBuffer buffer = ByteBuffer.wrap(BUFFER);
		buffer.putShort((short) Packet.SN_CTR);
		buffer.put((byte) Packet.TYPE_ACK);
		buffer.put((byte) 0); // ack包command设为空
		buffer.putShort((short) status);
		buffer.putShort((short) seq);
		return buffer.array();
	}

	@Override
	public String toString() {
		return "ACKPacket{" +
				"status=" + getStatusDesc(status) +
				", seq=" + seq +
				'}';
	}

	private String getStatusDesc(int status) {
		for (Field field : getClass().getDeclaredFields()) {
			if ((field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) > 0) {
				try {
					if (field.get(null) == Integer.valueOf(status)) {
						return field.getName();
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return status + "";
	}
}
