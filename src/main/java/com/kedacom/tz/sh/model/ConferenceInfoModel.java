package com.kedacom.tz.sh.model;

import lombok.Data;

/**
 * 会议信息
 * 
 * @author ysl
 *
 */
@Data
public class ConferenceInfoModel {
	/** 会议号 最大字符长度：48个字节 **/
	private String conf_id;
	/** 会议名称 最大字符长度：64个字节 **/
	private String name;
	/** 会议时长，0为永久会议 **/
	private Integer duration;
	/** 会议开始时间（ISO8601:2000格式表示） **/
	private String start_time;
	/** 会议结束时间（ISO8601:2000格式表示） **/
	private String end_time;
	/** 会议码率 **/
	private Integer bitrate;
	/** 自动结会(少于两个终端时自动结会) 0-不自动结束； 1-自动结束； **/
	private Integer auto_end;
	/** 是否开启初始化哑音 0-否； 1-是； **/
	private Integer mute;
	/** （必填）是否开启初始化静音 0-否； 1-是； **/
	private Integer silence;
}
