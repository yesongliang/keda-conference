package com.kedacom.tz.sh.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "终端摄像头控制操作请求参数")
public class ConfMtCameraParam {
	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 会议成员唯一标识 **/
	@ApiModelProperty(value = "会议成员ID")
	@NotNull(message = "不能为空")
	private String mtId;

	/** 摄像头状态 0-开始； 1-停止； **/
	@ApiModelProperty(value = "摄像头状态 0-开始； 1-停止；")
	@Min(value = 0, message = "不支持的操作状态")
	@Max(value = 1, message = "不支持的操作状态")
	private Integer state;

	/**
	 * 控制类型 1-上； 2-下； 3-左； 4-右； 5-上左； 6-上右； 7-下左； 8-下右； 9-视野小； 10-视野大； 11-调焦短；
	 * 12-调焦长； 13-亮度加； 14-亮度减； 15-自动调焦；
	 **/
	@ApiModelProperty(value = "控制类型 1-上； 2-下； 3-左； 4-右； 5-上左； 6-上右； 7-下左； 8-下右； 9-视野小； 10-视野大； 11-调焦短；12-调焦长； 13-亮度加； 14-亮度减； 15-自动调焦；")
	@Min(value = 1, message = "不支持的控制类型")
	@Max(value = 15, message = "不支持的控制类型")
	private Integer type;
}
