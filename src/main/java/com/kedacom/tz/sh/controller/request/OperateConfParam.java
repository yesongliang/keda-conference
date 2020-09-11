package com.kedacom.tz.sh.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "会场静音操作/会场哑音操作请求参数")
public class OperateConfParam {
	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 操作类型:1-指定会议双流源;2-取消会议双流源;3-终端静音操作;4-终端哑音操作; **/
	@ApiModelProperty(value = "操作类型:1-会场静音;2-取消会场静音;3-会场哑音;4-取消会场哑音;")
	@Min(value = 1, message = "不支持的操作类型")
	@Max(value = 4, message = "不支持的操作类型")
	private Integer type;
}
