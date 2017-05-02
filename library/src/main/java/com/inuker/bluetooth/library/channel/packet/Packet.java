package com.inuker.bluetooth.library.channel.packet;

import java.nio.ByteBuffer;

/**
 * Created by dingjikerbo on 17/4/14.
 */

/**
 * 包分流控包和数据包，流控包又包括指令包和ACK包
 */
public abstract class Packet {

	static final int BUFFER_SIZE = 20;

	static final byte[] BUFFER = new byte[BUFFER_SIZE];

	/**
	 * 流控包，非零都表示数据包
	 */
	static final int SN_CTR = 0;

	/**
	 * 指令包
	 */
	public static final int TYPE_CMD = 0x00;

	/**
	 * ACK包
	 */
	public static final int TYPE_ACK = 0x01;

	public static final String ACK = "ack";
	public static final String DATA = "data";
	public static final String CTR = "ctr";

	private static class Header {
		int sn;
		int type;
		int command;
		int parameter;
		byte[] value;
	}

	static class Bytes {
		byte[] value;

		// [start, end)
		int start, end;

		Bytes(byte[] value, int start) {
			this(value, start, value.length);
		}

		Bytes(byte[] value, int start, int end) {
			this.value = value;
			this.start = start;
			this.end = end;
		}

		int getSize() {
			return end - start;
		}
	}

	private static Header parse(byte[] bytes) {
		Header header = new Header();
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		header.sn = buffer.getShort();
		header.value = bytes;

		if (header.sn  == SN_CTR) {
			header.type = buffer.get();
			header.command = buffer.get();
			header.parameter = buffer.getInt();
		}

		return header;
	}

	public static Packet getPacket(byte[] bytes) {
		Header header = parse(bytes);

		switch (header.sn) {
			case 0:
				return getFlowPacket(header);

			default:
				return getDataPacket(header);
		}
	}

	/**
	 * 流控包分两种，流控和ACK
	 */
	private static Packet getFlowPacket(Header header) {
		int parameter = header.parameter;

		switch (header.type) {
			case 0:
				int frames = parameter >> 16;
				return new CTRPacket(frames);

			case 1:
				int status = parameter >> 16;
				int seq = parameter & 0xffff;
				return new ACKPacket(status, seq);

			default:
				return new InvalidPacket();
		}
	}

	private static Packet getDataPacket(Header header) {
		return new DataPacket(header.sn, new Bytes(header.value, 2));
	}

	public abstract String getName();

	public abstract byte[] toBytes();
}
