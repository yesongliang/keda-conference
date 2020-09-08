package com.kedacom.tz.sh.constant;

public interface ConferenceConstant {

	// common--------------------------------

	/** 1:成功，0：失败 **/
	String SUCCESS = "success";
	/** 错误码 **/
	String ERROR_CODE = "error_code";
	/** account_token **/
	String ACCOUNT_TOKEN = "account_token";
	/** params **/
	String PARAMS = "params";
	/** _method **/
	String METHOD = "_method";
	/** DELETE **/
	String METHOD_DELETE = "DELETE";
	/** PUT **/
	String METHOD_PUT = "PUT";
	/** 会议中的终端编号 **/
	String MT_ID = "mt_id";

	// private-------------------------------

	/** oauth中认证的软件key **/
	String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	/** oauth中认证软件key对应的value **/
	String OAUTH_CONSUMER_SECRET = "oauth_consumer_secret";

	/** 登录名 **/
	String USERNAME = "username";
	/** 登录密码 **/
	String PASSWORD = "password";
	/** 请求响应cookie **/
	String COOKIE = "Set-Cookie";
	/** 会议平台单点登录唯一标识 **/
	String SSO_COOKIE_KEY = "SSO_COOKIE_KEY";

	/** 版本信息 **/
	String VERSION = "version";

	/** 创建会议 **/
	String CONF_ID = "conf_id";

}
