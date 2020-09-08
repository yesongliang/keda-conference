package com.kedacom.tz.sh.controller.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "邀请成员参数")
public class ConferenceMemberParam {
	/** 帐号 最大字符长度：128个字节 **/
	@ApiModelProperty(value = "帐号；必选参数；最大字符长度：128个字节")
	private String account;
	/** 帐号类型 1-moid； 4-非系统邮箱； 5-e164号； 6-电话； 7-ip地址； 8-别名@ip(监控前端)； **/
	@ApiModelProperty(value = "帐号类型；必选参数；1-moid；4-非系统邮箱；5-e164号；6-电话；7-ip地址；8-别名@ip(监控前端)；")
	private Integer account_type;
	/** 呼叫协议 0-H323； 1-SIP； **/
	@ApiModelProperty(value = "呼叫协议；必选参数；0-H323； 1-SIP；")
	private Integer protocol;
}
