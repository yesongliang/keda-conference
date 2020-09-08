package com.kedacom.tz.sh.controller.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "创会请求参数")
public class AddMembersParam {

	/** 会议平台唯一标识 **/
	@NotNull(message = "不能为空")
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long platformId;

	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 邀请成员 **/
	@ApiModelProperty(value = "邀请成员")
	@Size(min = 1, message = "不能为空")
	private List<ConferenceMemberParam> invite_members;
}
