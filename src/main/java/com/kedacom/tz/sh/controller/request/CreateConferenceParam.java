package com.kedacom.tz.sh.controller.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创会参数定制
 * 
 * @author ysl
 *
 *
 *
 */
//TODO 暂时暴露这些参数
@Data
@ApiModel(description = "创会请求参数")
public class CreateConferenceParam {

	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;
	/**
	 * 会议名称 创建虚拟会议室时可填 1.字符限制： a.不支持输入特殊字符：% & * ^ ~ ' " " ? / \ < > | ` " $
	 * b.且首字符和尾字符不支持输入，下划线（_） 减号（-） 小数点（.） @ （除首尾字符可以输入） 2.最大字符长度：64个字节
	 **/
	@ApiModelProperty(value = "会议名称")
	@NotNull(message = "不能为空")
	private String name;
	/** 会议时长 创建虚拟会议室时可填，0为永久会议 **/
	@ApiModelProperty(value = "会议时长，0为永久会议，单位分钟")
	@Min(value = 0, message = "不能为负数")
	private Integer duration;
	/**
	 * 会议中无终端时，是否自动结会，永久会议时默认为0 0-否； 1-是；
	 **/
	@ApiModelProperty(value = "会议中无终端时，是否自动结会, 0-否； 1-是；")
	@Min(value = 0, message = "无效参数")
	@Max(value = 1, message = "无效参数")
	private Integer auto_end;
	/**
	 * 主视频格式 1-全高清(2M 1080P@60fps); 2-高清(1M 720P@30fps); 3-标清(256K W4CIF@25fps);
	 * 4-流畅(192K WCIF@25fps);
	 **/
	@ApiModelProperty(value = "主视频格式 1-全高清(2M 1080P@60fps); 2-高清(1M 720P@30fps); 3-标清(256K W4CIF@25fps); 4-流畅(192K WCIF@25fps);")
	@Min(value = 1, message = "不支持的主视频格式")
	@Max(value = 4, message = "不支持的主视频格式")
	private Integer video_format;

	/** 邀请成员 **/
	@ApiModelProperty(value = "邀请成员；可选参数，默认：无")
	private List<ConferenceMemberParam> invite_members;

}
