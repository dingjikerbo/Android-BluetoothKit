package com.inuker.bluetooth.library.advertise;

import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a byte array representing a BLE advertisement into a number of
 * "Payload Data Units" (PDUs).
 */
public class BleAdvertisement {
	private static final String TAG = "BleAdvertisement";
	private List<Pdu> mPdus;
	private byte[] mBytes;

    public BleAdvertisement(byte[] scanRecord) {
        if (!ByteUtils.isEmpty(scanRecord)) {
            mBytes = scanRecord;

            try {
                mPdus = parsePdus(mBytes);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

	private List<Pdu> parsePdus(byte[] bytes) {
		ArrayList<Pdu> pdus = new ArrayList<Pdu>();
		Pdu pdu = null;
		int index = 0;
		do {
			pdu = Pdu.parse(bytes, index);
			if (pdu != null) {
				index = index + pdu.getDeclaredLength() + 1;
				pdus.add(pdu);
			}
		} while (pdu != null && index < bytes.length);
		return pdus;
	}

	/**
	 * The list of PDUs inside the advertisement
	 * 
	 * @return
	 */
	public List<Pdu> getPdus() {
		return mPdus;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

        if (mPdus != null) {
            for (int i = 0; i < mPdus.size(); i++) {
                Pdu pdu = mPdus.get(i);
                sb.append(pdu.toString()).append("\n");
            }
        }

		return sb.toString();
	}
}