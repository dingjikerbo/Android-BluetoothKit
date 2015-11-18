package com.inuker.bluetooth.advertise;

import java.util.ArrayList;
import java.util.List;

import com.inuker.bluetooth.utils.BluetoothConstants;
import com.inuker.bluetooth.utils.ByteUtils;
import com.inuker.bluetooth.utils.ListUtils;

public class BLEAdvertisement {

	private byte[] mBytes;
	private List<Pdu> mPdus;
	
	private Pdu mMyPdu;
	
	
	@SuppressWarnings("unused")
	private MyBleAdvPacket mMyBleAdvPacket;

	public BLEAdvertisement(byte[] scanRecord) {
		if (!ByteUtils.isEmpty(scanRecord)) {
			mBytes = scanRecord;

			try {
				mPdus = parsePdus(mBytes);
				processMyPdu();
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
				index += pdu.getDeclaredLength() + 1;
				pdus.add(pdu);
			}
		} while (pdu != null && index < bytes.length);
		return pdus;
	}
	
	private void processMyPdu() {
        if (ListUtils.isEmpty(mPdus)) {
            return;
        }

        PacketReader reader = new PacketReader(mBytes);

		for (Pdu pdu : mPdus) {
            reader.setCurrentPdu(pdu);
			
			switch (pdu.getType() & 0xff) {
			case 0x16:
				int serviceId = reader.getShort();
				if (serviceId == BluetoothConstants.INUKER_UUID) {
					mMyPdu = pdu;
					mMyBleAdvPacket = new MyBleAdvPacket(reader);
				}

                break;
			}
		}
	}
	
	public Pdu getMyPdu() {
        return mMyPdu;
    }

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
                sb.append(pdu.toString());
                
                if (i != mPdus.size() - 1) {
                	sb.append("\n");
                }
            }
        }
		
		return sb.toString();
	}
}
