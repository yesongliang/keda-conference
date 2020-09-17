package com.kedacom.tz.sh.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "终端监控相关操作请求参数")
public class MonitorOperateParam {

	/** 会议平台唯一标识 **/
	@ApiModelProperty(value = "会议平台唯一标识")
	@NotNull(message = "不能为空")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 操作类型：1-取消监控；2-监控心跳保活；3-监控请求关键帧 **/
	@ApiModelProperty(value = "操作类型：1-取消监控；2-监控心跳保活；3-监控请求关键帧")
	@Min(value = 1, message = "不支持的操作类型")
	@Max(value = 3, message = "不支持的操作类型")
	private Integer type;

	/** 目的ip地址 **/
	@ApiModelProperty(value = "目的ip地址")
	@Pattern(regexp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$", message = "不符合格式")
	private String ip;

	/** 目的rtp端口 **/
	@ApiModelProperty(value = "目的rtp端口")
	@Min(value = 1, message = "超出有效范围")
	@Max(value = 65535, message = "超出有效范围")
	private Integer port;

}
