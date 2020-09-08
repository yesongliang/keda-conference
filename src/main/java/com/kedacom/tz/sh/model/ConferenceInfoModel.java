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
	/** 会议名称 最大字符长度：64个字节 **/
	private String name;
	/** 会议号 最大字符长度：48个字节 **/
	private String conf_id;
	/** 会议类型 0-传统会议； 1-端口会议； **/
	private Integer conf_type;
	/** 会议时长，0为永久会议 **/
	private Integer duration;
	// TODO 待续...
}
