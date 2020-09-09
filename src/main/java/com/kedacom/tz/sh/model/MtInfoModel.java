package com.kedacom.tz.sh.model;

import lombok.Data;

/**
 * 会议终端信息
 * 
 * @author ysl
 *
 */
@Data
public class MtInfoModel {
	/** 终端别名 最大字符长度：128个字节 **/
	private String alias;
	/** 终端号 最大字符长度：48个字节 **/
	private String mt_id;
	/** 终端IP **/
	private String ip;
	/** 终端e164号 **/
	private String e164;
	/** 终端类型 1-普通终端； 3-电话终端； 5-卫星终端； 7-下级会议； 8-上级会议； **/
	private Integer type;
	/** 是否在线 0-否； 1-是； **/
	private Integer online;
	/** 是否静音 0-否； 1-是； **/
	private Integer silence;
	/** 是否哑音 0-否； 1-是； **/
	private Integer mute;
	/** 是否在发送双流 0-否； 1-是； **/
	private Integer dual;
	/** 是否在混音 0-否； 1-是； **/
	private Integer mix;
	/** 是否在合成 0-否； 1-是； **/
	private Integer vmp;
	/** 是否在选看 0-否； 1-是； **/
	private Integer inspection;
	/** 是否在录像 0-否； 1-是； **/
	private Integer rec;
	/** 是否在轮询 0-否； 1-是； **/
	private Integer poll;
	/** 是否在上传 0-否； 1-是； **/
	private Integer upload;
	/** 呼叫协议，获取下级会议终端信息时不返回 0-323； 1-sip； **/
	private Integer protocol;
	/** 呼叫模式，获取下级会议终端信息时不返回 0-手动； 2-自动； 3-追呼； **/
	private Integer call_mode;
	/** 接收音量 **/
	private Integer rcv_volume;
	/** 发送音量 **/
	private Integer snd_volume;

}
