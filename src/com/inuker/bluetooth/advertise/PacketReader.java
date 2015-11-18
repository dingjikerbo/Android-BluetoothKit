package com.inuker.bluetooth.advertise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.inuker.bluetooth.utils.StringUtils;

/**
 * Created by liwentian on 2015/9/21.
 */
public class PacketReader {

    private Pdu mCurrentPdu;
    private ByteBuffer mByteBuffer;

    public PacketReader(byte[] bytes) {
        mByteBuffer = ByteBuffer.wrap(bytes).order(
                ByteOrder.LITTLE_ENDIAN);
    }

    public void position(int position) {
        mByteBuffer.position(position);
    }

    public int getShort() {
        return mByteBuffer.getShort() & 0xffff;
    }

    public int getByte() {
        return mByteBuffer.get() & 0xff;
    }

    public boolean getBit(int n, int index) {
        return (n & (1 << index)) != 0;
    }

    public int getInt(int n, int start, int end) {
        return (n >> start) & ((1 << (end - start + 1)) - 1);
    }

    public String getMac() {
        String[] texts = new String[6];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = String.format("%x", getByte());
        }
        return StringUtils.join(texts, ":");
    }

    public void setCurrentPdu(Pdu pdu) {
        mCurrentPdu = pdu;
        position(mCurrentPdu.getStartIndex());
    }

    public int getLastShort() {
        position(mCurrentPdu.getEndIndex() - 1);
        return getShort();
    }

    public boolean overflow() {
        return mByteBuffer.position() > mCurrentPdu.getEndIndex() + 1;
    }
}
