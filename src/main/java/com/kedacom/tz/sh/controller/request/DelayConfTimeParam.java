package com.kedacom.tz.sh.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "延长会议时间操作请求参数")
public class DelayConfTimeParam {

	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 延长的时间，单位：分钟 **/
	@ApiModelProperty(value = "延长的时间，单位：分钟")
	@Min(value = 1, message = "必须大于零")
	private Integer delayTime;
}
