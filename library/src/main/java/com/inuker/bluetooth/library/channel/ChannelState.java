package com.inuker.bluetooth.library.channel;

/**
 * Created by dingjikerbo on 17/4/14.
 */

public enum ChannelState {

	IDLE,

	/**
	 * 准备就绪
	 */
	READY,

	/**
	 * 等待发送帧ack
	 */
	WAIT_START_ACK,

	/**
	 * 正在发送数据帧
	 */
	WRITING,

	/**
	 * 丢失包同步阶段
	 */
	SYNC,

	/**
	 * 同步
	 */
	SYNC_ACK,

	/**
	 * 同步阶段发送了ACK，等待对端回包
	 */
	SYNC_WAIT_PACKET,

	/**
	 * 接收端
	 */
	READING,
}
