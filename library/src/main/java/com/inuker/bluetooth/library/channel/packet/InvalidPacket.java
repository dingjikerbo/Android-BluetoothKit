package com.inuker.bluetooth.library.channel.packet;

/**
 * Created by dingjikerbo on 17/4/16.
 */

public class InvalidPacket extends Packet {

	@Override
	public String toString() {
		return "InvalidPacket{}";
	}

	@Override
	public String getName() {
		return "invalid";
	}

	@Override
	public byte[] toBytes() {
		return new byte[0];
	}
}
