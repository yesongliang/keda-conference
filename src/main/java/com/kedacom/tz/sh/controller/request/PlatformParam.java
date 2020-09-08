package com.kedacom.tz.sh.controller.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "会议平台管理请求操作参数实体类")
public class PlatformParam {

	/** 会议平台地址 **/
	@ApiModelProperty(value = "会议平台地址")
	@Pattern(regexp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$", message = "不符合格式")
	private String ip;
	/** 会议平台端口 **/
	@ApiModelProperty(value = "会议平台端口")
	@Min(value = 1, message = "小于1")
	@Max(value = 65535, message = "大于65535")
	private Integer port;
	/** oauth中认证的软件key **/
	@ApiModelProperty(value = "oauth中认证的软件key")
	@NotBlank(message = "不能为空")
	private String oauth_consumer_key;
	/** oauth中认证软件key对应的value **/
	@ApiModelProperty(value = "oauth中认证软件key对应的value")
	@NotBlank(message = "不能为空")
	private String oauth_consumer_secret;
	/** 用户名 **/
	@ApiModelProperty(value = "用户名")
	@NotBlank(message = "不能为空")
	private String username;
	/** 密码 **/
	@ApiModelProperty(value = "密码")
	@NotBlank(message = "不能为空")
	private String password;

}
