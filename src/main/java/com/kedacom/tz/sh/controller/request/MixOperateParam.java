package com.kedacom.tz.sh.controller.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "会议混音相关操作请求参数")
public class MixOperateParam {
	/** 会议平台唯一标识 **/
	@ApiModelProperty(value = "会议平台唯一标识")
	@NotNull(message = "不能为空")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 会议成员唯一标识集合 **/
	@ApiModelProperty(value = "会议成员唯一标识集合；开启会议混音、添加混音成员、删除混音成员操作时必需")
	private List<String> mtIds;

	/** 操作类型:1-开始会议混音；2-结束会议混音；3-添加会议混音成员；4-删除会议混音成员 **/
	@ApiModelProperty(value = "操作类型:1-开始会议混音；2-结束会议混音；3-添加会议混音成员；4-删除会议混音成员")
	@Min(value = 1, message = "不支持的操作类型")
	@Max(value = 4, message = "不支持的操作类型")
	private Integer type;

	/** 混音模式 1-智能混音； 2-定制混音； **/
	@ApiModelProperty(value = "混音模式 1-智能混音； 2-定制混音；开启混音操作时必需")
	private Integer mode;

}
