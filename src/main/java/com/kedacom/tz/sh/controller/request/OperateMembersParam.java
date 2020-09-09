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
@ApiModel(description = "批量删除/呼叫/挂断终端请求参数")
public class OperateMembersParam {

	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;

	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 会议成员唯一标识集合 **/
	@ApiModelProperty(value = "会议成员唯一标识集合")
	@Size(min = 1, message = "不能为空")
	private List<String> mtIds;

	/** 操作类型:1-批量删除终端;2-批量呼叫终端;3-批量挂断终端 **/
	@ApiModelProperty(value = "操作类型:1-批量删除终端;2-批量呼叫终端;3-批量挂断终端 ")
	@Min(value = 1, message = "不支持的操作类型")
	@Max(value = 3, message = "不支持的操作类型")
	private Integer type;

}
