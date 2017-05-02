package com.inuker.bluetooth.library.channel.packet;

import com.inuker.bluetooth.library.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by dingjikerbo on 17/4/16.
 */

public class DataPacket extends Packet {

	private int seq;

	private Bytes bytes;

	// only last frame has crc
	private byte[] crc;

	public DataPacket(int seq, Bytes bytes) {
		this.seq = seq;
		this.bytes = bytes;
	}

	public DataPacket(int seq, byte[] value, int start, int end) {
		this(seq, new Bytes(value, start, end));
	}

	public int getSeq() {
		return seq;
	}

	public int getDataLength() {
		return bytes.getSize();
	}

	@Override
	public String getName() {
		return DATA;
	}

	public void setLastFrame() {
		bytes.end -= 2;
		crc = ByteUtils.get(bytes.value, bytes.end, 2);
	}

	public byte[] getCrc() {
		return crc;
	}

	@Override
	public byte[] toBytes() {
		ByteBuffer buffer;

		int packetSize = getDataLength() + 2;

		if (packetSize == BUFFER_SIZE) {
			Arrays.fill(BUFFER, (byte) 0);
			buffer = ByteBuffer.wrap(BUFFER);
		} else {
			buffer = ByteBuffer.allocate(packetSize);
		}

		buffer.putShort((short) seq);
		fillByteBuffer(buffer);

		return buffer.array();
	}

	public void fillByteBuffer(ByteBuffer buffer) {
		buffer.put(bytes.value, bytes.start, getDataLength());
	}

	@Override
	public String toString() {
		return "DataPacket{" +
				"seq=" + seq +
				", size=" + bytes.getSize() +
//				", value=0x" + ByteUtils.byteToString(value) +
				'}';
	}
}
