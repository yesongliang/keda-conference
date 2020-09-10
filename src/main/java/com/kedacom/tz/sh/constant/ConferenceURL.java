package com.kedacom.tz.sh.constant;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会议请求URL
 * 
 * @author ysl
 *
 */
@AllArgsConstructor
@Getter
public enum ConferenceURL {

	TOKEN("token获取", "http://%s:%s/api/v1/system/token", HttpMethod.POST),

	// /api/v1/system/token/{account_token}/heartbeat
	HEARTBEAT_TOKEN("保持token心跳", "http://%s:%s/api/v1/system/%s/heartbeat", HttpMethod.POST),

	LOGIN("登陆", "http://%s:%s/api/v1/system/login", HttpMethod.POST),

	HEARTBEAT_USER("保持用户心跳", "http://%s:%s/api/v1/system/heartbeat", HttpMethod.POST),

	VERSION("获取登录认证API版本信息", "http://%s:%s/api/system/version", HttpMethod.GET),

	PUBLISH("访问CometD服务器的URL", "http://%s:%s/api/v1/publish", null),

	CREATE_CONF("创建会议", "http://%s:%s/api/v1/mc/confs", HttpMethod.POST),

	/// api/v1/mc/confs/{conf_id}
	END_CONF("结束会议", "http://%s:%s/api/v1/mc/confs/%s", HttpMethod.DELETE),

	GET_CONF_LIST("获取视频会议列表", "http://%s:%s/api/v1/vc/confs?account_token=%s&start=0&count=0", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}
	GET_CONF_INFO("获取视频会议信息", "http://%s:%s/api/v1/vc/confs/%s?account_token=%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/mts
	GET_CONF_MT_LIST("获取本级会议终端列表", "http://%s:%s/api/v1/vc/confs/%s/mts?account_token=%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/mts/{mt_id}
	GET_CONF_MT_INFO("获取与会终端信息", "http://%s:%s/api/v1/vc/confs/%s/mts/%s?account_token=%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/mts
	ADD_MTS("批量添加本级终端", "http://%s:%s/api/v1/vc/confs/%s/mts", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/mts
	DELETE_MTS("批量删除终端", "http://%s:%s/api/v1/vc/confs/%s/mts", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/online_mts
	ONLINE_MTS("批量呼叫终端", "http://%s:%s/api/v1/vc/confs/%s/online_mts", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/online_mts
	OFFLINE_MTS("批量挂断终端", "http://%s:%s/api/v1/vc/confs/%s/online_mts", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/chairman
	GET_CHAIRMAN("获取会议主席", "http://%s:%s/api/v1/vc/confs/%s/chairman", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/chairman
	PUT_CHAIRMAN("指定会议主席", "http://%s:%s/api/v1/vc/confs/%s/chairman", HttpMethod.PUT),

	;

	/** 请求描述 **/
	private String describe;
	/** 请求URL **/
	private String url;
	/** 请求方法 **/
	private HttpMethod httpMethod;
	/** 参数格式统一为:MediaType.APPLICATION_FORM_URLENCODED **/
//	private MediaType mediaType;
}
