package com.kedacom.tz.sh.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 发送短消息
 * 
 * @author ysl
 *
 */
@Data
public class ConfSMSModel {

	/** 接收消息的终端数组 **/
	private List<Mt> mts;

	/** 消息内容,发送空消息即停止短消息 最大字符长度：1500个字节 **/
	private String message;

	/** 滚动次数 1-255 新版本终端255为无限轮询; **/
	private Integer roll_num;

	/** 滚动速度 1-慢速； 2-中速； 3-快速； **/
	private Integer roll_speed;

	/** 短消息类型 0-自右至左滚动； 1-翻页滚动； 2-全页滚动； **/
	private Integer type;

	@Data
	private class Mt {
		/** 终端号 最大字符长度：48个字节 **/
		private String mt_id;
	}

	public void addMt(String mtId) {
		if (this.mts == null) {
			this.mts = new ArrayList<>();
		}
		Mt mt = new Mt();
		mt.setMt_id(mtId);
		this.mts.add(mt);
	}
}
