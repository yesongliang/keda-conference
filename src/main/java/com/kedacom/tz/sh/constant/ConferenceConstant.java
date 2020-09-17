package com.kedacom.tz.sh.constant;

public interface ConferenceConstant {

	// config--------------------------------
	String DISTRIBUTE_ENABLE = "distribute.enable";
	String DISTRIBUTE_CAPACITY = "distribute.capacity";

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
	/** 操作值 **/
	String VALUE = "value";

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

	/** 延长会议时间 **/
	String DELAY_TIME = "delay_time";

	/** 终端摄像头控制 **/
	String STATE = "state";
	String TYPE = "type";

	/** 修改终端音量 **/
	String VOL_MODE = "vol_mode";
	String VOL_VALUE = "vol_value";

}
