package com.kedacom.tz.sh.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 添加本级终端
 * 
 * @author ysl
 *
 */
@Data
public class AddMtsModel {

	private List<MtModel> mts;

	@Data
	public class MtModel {
		/** (必填)帐号 最大字符长度：128个字节，终端E164号,IP或电话号码 **/
		private String account;
		/**
		 * (必填)帐号类型 1-moid； 4-非系统邮箱； 5-e164号； 6-电话； 7-ip地址； 8-别名@ip(监控前端)；
		 **/
		private Integer account_type;
		/** (必填)呼叫码率 **/
		private Integer bitrate;
		/**
		 * (必填)呼叫协议 0-H323； 1-SIP；
		 **/
		private Integer protocol;
		/**
		 * 是否强制呼叫，默认是0 0-不强呼； 1-强呼；
		 **/
		private Integer forced_call;
		/**
		 * 呼叫模式，默认为创会时设置的呼叫模式 0-手动； 2-自动； 3-追呼；
		 **/
		private Integer call_mode;
	}

	public void addMt(MtModel mt) {
		if (this.mts == null) {
			this.mts = new ArrayList<>();
		}
		this.mts.add(mt);
	}
}
