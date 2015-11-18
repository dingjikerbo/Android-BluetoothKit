
package com.inuker.bluetooth.advertise;

import com.inuker.bluetooth.utils.BluetoothUtils;

public class MyBleAdvPacket {

    public FrameControl frameControl;

    public int productId;

    public int frameCounter;

    public String mac;

    public Capability capability;

    public Event event;

    public boolean valid;

    public int position;

    public MyBleAdvPacket(PacketReader reader) {
        try {
            frameControl = new FrameControl(reader);

            if (!BluetoothUtils.isProtocolSupported(frameControl.version)) {
                return;
            }

            productId = reader.getShort();
            frameCounter = reader.getByte();

            if (frameControl.withMac) {
                mac = reader.getMac();
            }

            if (frameControl.withCapability) {
                capability = new Capability(reader);
            }

            if (frameControl.withEvent) {
                event = new Event(reader);
            }

            valid = !reader.overflow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBinding() {
        return valid && frameControl.binding;
    }

    public static class FrameControl {
        /*
         * 1 未绑定，还在出厂设置, 0 已经跟绑定过用户或不需要绑定
         */
        public boolean factoryNew;

        /*
         * 1 当前已连接, 0 当前未连接
         */
        public boolean connected;

        /*
         * 1 当前是Central，0，当前是Peripheral,如果bit1为1，则此位无效
         */
        public boolean central;

        /*
         * 1 该包已加密，0，该包未加密
         */
        public boolean encrypted;

        /*
         * 1 Frame control后包含6个byte的MAC地址，0 不包含6个BYTE的MAC地址
         */
        public boolean withMac;

        /*
         * 1 包含capability, 0 不包含capability
         */
        public boolean withCapability;

        /*
         * 1 包含事件，0,不包含事件
         */
        public boolean withEvent;

        /*
         * 1 包含厂商自定义数据，0,不包含自定义数据
         */
        public boolean withCustomData;

        /*
         * 1 包含厂商自定义智能家庭副标题展示数据，0,不包含副标题数据
         */
        public boolean withSubtitle;

        /*
         * 1 是一个绑定确认包，0，不是绑定的确认包
         */
        public boolean binding;

        /*
         * 协议版本号
         */
        public int version;

        public FrameControl(PacketReader reader) {
            int firstByte = reader.getByte();
            factoryNew = reader.getBit(firstByte, 0);
            connected = reader.getBit(firstByte, 1);
            central = reader.getBit(firstByte, 2);
            encrypted = reader.getBit(firstByte, 3);
            withMac = reader.getBit(firstByte, 4);
            withCapability = reader.getBit(firstByte, 5);
            withEvent = reader.getBit(firstByte, 6);
            withCustomData = reader.getBit(firstByte, 7);

            int secondByte = reader.getByte();
            withSubtitle = reader.getBit(secondByte, 0);
            binding = reader.getBit(secondByte, 1);
            version = reader.getInt(secondByte, 4, 7);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("");
            sb.append("factoryNew = " + factoryNew).append("\n");
            sb.append("connected = " + connected).append("\n");
            sb.append("central = " + central).append("\n");
            sb.append("encrypted = " + encrypted).append("\n");
            sb.append("withMac = " + withMac).append("\n");
            sb.append("withCapability = " + withCapability).append("\n");
            sb.append("withEvent = " + withEvent).append("\n");
            sb.append("withCustomData = " + withCustomData).append("\n");
            sb.append("withSubtitle = " + withSubtitle).append("\n");
            sb.append("binding = " + binding).append("\n");
            sb.append("version = " + version).append("\n");
            return sb.toString();
        }
    }

    public static class Capability {
        /*
         * 1 有建立连接能力, 0 不能建立连接
         */
        public boolean connectable;

        /*
         * 1 能做蓝牙的Central, 0 不能做Central
         */
        public boolean centralable;

        /*
         * 1 有加密的能力，0，没有加密的能力
         */
        public boolean encryptable;

        /*
         * 0， 无确认能力，1，前绑定，2，后绑定
         */
        public int bindable;

        public Capability(PacketReader reader) {
            int capByte = reader.getByte();
            connectable = reader.getBit(capByte, 0);
            centralable = reader.getBit(capByte, 1);
            encryptable = reader.getBit(capByte, 2);
            bindable = reader.getInt(capByte, 3, 4);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("connectable = " + connectable).append("\n");
            sb.append("centralable = " + centralable).append("\n");
            sb.append("encryptable = " + encryptable).append("\n");
            sb.append("bindable = " + bindable).append("\n");
            return sb.toString();
        }
    }

    public static class Event {
        public int eventId;
        public int eventData;

        public Event(PacketReader reader) {
            eventId = reader.getShort();
            eventData = reader.getByte();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("");
            sb.append("eventId = " + eventId).append("\n");
            sb.append("eventData = " + eventData).append("\n");
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (frameControl != null) {
            sb.append(frameControl.toString()).append("\n");
        }

        sb.append("productId: " + productId).append("\n");
        sb.append("frameCounter: " + frameCounter).append("\n");

        if (frameControl != null && frameControl.withMac) {
            sb.append("mac: " + mac).append("\n");
        }

        if (capability != null) {
            sb.append(capability.toString()).append("\n");
        }

        if (event != null) {
            sb.append(event.toString()).append("\n");
        }

        sb.append("valid: " + valid).append("\n");

        return sb.toString();
    }
}
