package com.kedacom.tz.sh.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "修改终端音量操作请求参数")
public class ConfMtVolumeParam {
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

	/** 终端音量设备 1-扬声器； 2-麦克风； **/
	@ApiModelProperty(value = "终端音量设备 1-扬声器； 2-麦克风；")
	@Min(value = 1, message = "不支持的设备类型")
	@Max(value = 2, message = "不支持的设备类型")
	private Integer mode;

	/** 音量 0-35 **/
	@ApiModelProperty(value = "音量 0-35")
	@Min(value = 0, message = "音量超出范围")
	@Max(value = 35, message = "音量超出范围")
	private Integer value;
}
