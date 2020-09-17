package com.kedacom.tz.sh.controller.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "发送短消息请求参数")
public class ConfSMSParam {
	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 会议成员唯一标识集合 **/
	@ApiModelProperty(value = "会议成员唯一标识集合")
	@Size(min = 1, message = "不能为空")
	private List<String> mtIds;

	/** 消息内容,发送空消息即停止短消息 最大字符长度：1500个字节 **/
	@ApiModelProperty(value = "消息内容,发送空消息即停止短消息")
	private String message;

	/** 滚动次数 1-255 新版本终端255为无限轮询; **/
	@ApiModelProperty(value = "滚动次数 1-255 新版本终端255为无限轮询;")
	@Min(value = 1, message = "不支持的滚动次数")
	@Max(value = 255, message = "不支持的滚动次数")
	private Integer rollNum;

	/** 滚动速度 1-慢速； 2-中速； 3-快速； **/
	@ApiModelProperty(value = "滚动速度 1-慢速； 2-中速； 3-快速;")
	@Min(value = 1, message = "不支持的滚动速度")
	@Max(value = 3, message = "不支持的滚动速度")
	private Integer rollSpeed;

	/** 短消息类型 0-自右至左滚动； 1-翻页滚动； 2-全页滚动； **/
	@ApiModelProperty(value = "短消息类型 0-自右至左滚动； 1-翻页滚动； 2-全页滚动;")
	@Min(value = 0, message = "不支持的短消息类型")
	@Max(value = 2, message = "不支持的短消息类型")
	private Integer type;
}
