package com.kedacom.tz.sh.controller.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "会议平台信息响应实体类")
public class PlatformVo {

	/** 会议平台唯一标识（ip转long生成） **/
	@ApiModelProperty(value = "会议平台唯一标识")
	private Long id;
	/** 会议平台地址 **/
	@ApiModelProperty(value = "会议平台IP地址")
	private String ip;
	/** 会议平台端口 **/
	@ApiModelProperty(value = "会议平台端口")
	private Integer port;
	/** oauth中认证的软件key **/
	@ApiModelProperty(value = "oauth中认证的软件key")
	private String oauth_consumer_key;
	/** oauth中认证软件key对应的value **/
	@ApiModelProperty(value = "oauth中认证软件key对应的value")
	private String oauth_consumer_secret;
	/** 用户名 **/
	@ApiModelProperty(value = "用户名")
	private String username;
	/** 密码 **/
	@ApiModelProperty(value = "密码")
	private String password;
	/** 登录是否成功 **/
	@ApiModelProperty(value = "登录状态：true-已登录;false-未登录")
	private boolean isLogin;
	/** cometD连接否成功 **/
	@ApiModelProperty(value = "cometD连接状态：true-已登录;false-未登录")
	private boolean isConnected;
	/** 接口获取到的token **/
	@ApiModelProperty(value = "接口获取到的token")
	private String token;
	/** 登录成功返回的cookie **/
	@ApiModelProperty(value = "登录成功返回的cookie")
	private List<String> cookie;

}
