package com.inuker.bluetooth.library.channel;

/**
 * Created by dingjikerbo on 17/4/14.
 */

public enum ChannelEvent {

	/**
	 * 收到流控包
	 */
	RECV_CTR,

	/**
	 * 发送流控包
	 */
	SEND_CTR,

	/**
	 * 收到数据包
	 */
	RECV_DATA,

	/**
	 * 发送数据包
	 */
	SEND_DATA,

	/**
	 * 收到ACK包
	 */
	RECV_ACK,

	/**
	 * 发送ACK包
	 */
	SEND_ACK,
}
