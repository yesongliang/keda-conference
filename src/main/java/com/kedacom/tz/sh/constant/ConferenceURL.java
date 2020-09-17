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
	GET_CHAIRMAN("获取会议主席", "http://%s:%s/api/v1/vc/confs/%s/chairman?account_token=%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/chairman
	PUT_CHAIRMAN("指定会议主席", "http://%s:%s/api/v1/vc/confs/%s/chairman", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/speaker
	GET_SPEAKER("获取会议发言人", "http://%s:%s/api/v1/vc/confs/%s/speaker?account_token=%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/speaker
	PUT_SPEAKER("指定会议发言人", "http://%s:%s/api/v1/vc/confs/%s/speaker", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/dualstream
	PUT_DUALSTREAM("指定会议双流源", "http://%s:%s/api/v1/vc/confs/%s/dualstream", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/dualstream
	DELETE_DUALSTREAM("取消会议双流源", "http://%s:%s/api/v1/vc/confs/%s/dualstream", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/dualstream
	GET_DUALSTREAM("获取会议双流源", "http://%s:%s/api/v1/vc/confs/%s/dualstream?account_token=%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/delay
	DELAY_CONF("延长会议时间", "http://%s:%s/api/v1/vc/confs/%s/delay", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/silence
	CONF_SILENCE("会场静音操作", "http://%s:%s/api/v1/vc/confs/%s/silence", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/mute
	CONF_MUTE("会场哑音操作", "http://%s:%s/api/v1/vc/confs/%s/mute", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/sms
	CONF_SMS("发送短消息", "http://%s:%s/api/v1/vc/confs/%s/sms", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/mixs/{mix_id}，会议主混音器，默认mix_id为1
	GET_CONF_MIX("获取会议混音信息", "http://%s:%s/api/v1/vc/confs/%s/mixs/1", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/mixs
	START_CONF_MIX("开启会议混音", "http://%s:%s/api/v1/vc/confs/%s/mixs", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/mixs/{mix_id}，会议主混音器，默认mix_id为1
	STOP_CONF_MIX("停止会议混音", "http://%s:%s/api/v1/vc/confs/%s/mixs/1", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/mixs/{mix_id}/members，会议主混音器，默认mix_id为1
	CONF_MIX_MEMBER_ADD("添加混音成员", "http://%s:%s/api/v1/vc/confs/%s/mixs/1/members", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/mixs/{mix_id}/members，会议主混音器，默认mix_id为1
	CONF_MIX_MEMBER_DELETE("删除混音成员", "http://%s:%s/api/v1/vc/confs/%s/mixs/1/members", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/vmps/{vmp_id},会议主画面合成，默认vmp_id为1
	GET_CONF_VMP("获取会议画面合成信息", "http://%s:%s/api/v1/vc/confs/%s/vmps/1", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/vmps
	START_CONF_VMP("开启会议画面合成", "http://%s:%s/api/v1/vc/confs/%s/vmps", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/vmps/{vmp_id},会议主画面合成，默认vmp_id为1
	MODIFY_CONF_VMP("修改会议画面合成", "http://%s:%s/api/v1/vc/confs/%s/vmps/1", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/vmps/{vmp_id}，会议主画面合成，默认vmp_id为1
	STOP_CONF_VMP("停止会议画面合成", "http://%s:%s/api/v1/vc/confs/%s/vmps/1", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/mts/{mt_id}/silence
	CONF_MT_SILENCE("终端静音操作", "http://%s:%s/api/v1/vc/confs/%s/mts/%s/silence", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/mts/{mt_id}/mute
	CONF_MT_MUTE("终端哑音操作", "http://%s:%s/api/v1/vc/confs/%s/mts/%s/mute", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/mts/{mt_id}/volume
	CONF_MT_VOLUME("修改终端音量", "http://%s:%s/api/v1/vc/confs/%s/mts/%s/volume", HttpMethod.PUT),

	// /api/v1/vc/confs/{conf_id}/mts/{mt_id}/camera
	CONF_MT_CAMERA("终端摄像头控制", "http://%s:%s/api/v1/vc/confs/%s/mts/%s/camera", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/monitors/{dst_ip}/{dst_port}
	GET_MONITORS_INFO("获取监控信息", "http://%s:%s/api/v1/vc/confs/%s/monitors/%s/%s", HttpMethod.GET),

	// /api/v1/vc/confs/{conf_id}/monitors
	MONITORS_OPERATE("监控操作", "http://%s:%s/api/v1/vc/confs/%s/monitors", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/monitors/{dst_ip}/{dst_port}
	MONITORS_CANCEL("取消监控", "http://%s:%s/api/v1/vc/confs/%s/monitors/%s/%s", HttpMethod.DELETE),

	// /api/v1/vc/confs/{conf_id}/neediframe/monitors
	MONITORS_NEEDIFRAME("监控请求关键帧", "http://%s:%s/api/v1/vc/confs/%s/neediframe/monitors", HttpMethod.POST),

	// /api/v1/vc/confs/{conf_id}/monitors_heartbeat
	MONITORS_HEARTBEAT("监控心跳保活", "http://%s:%s/api/v1/vc/confs/%s/monitors_heartbeat", HttpMethod.POST),

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
