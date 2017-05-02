package com.inuker.bluetooth.library.channel.packet;

import java.nio.ByteBuffer;

/**
 * Created by dingjikerbo on 17/4/15.
 */

public class CTRPacket extends Packet {

	private int frameCount;

	public CTRPacket(int frameCount) {
		this.frameCount = frameCount;
	}

	public int getFrameCount() {
		return frameCount;
	}

	@Override
	public String getName() {
		return CTR;
	}

	@Override
	public byte[] toBytes() {
		ByteBuffer buffer = ByteBuffer.wrap(BUFFER);
		buffer.putShort((short) Packet.SN_CTR);
		buffer.put((byte) Packet.TYPE_CMD);
		buffer.put((byte) 0); // ctr包command暂设为空
		buffer.putShort((short) frameCount);
		return buffer.array();
	}

	@Override
	public String toString() {
		return "FlowPacket{" +
				"frameCount=" + frameCount +
				'}';
	}
}
