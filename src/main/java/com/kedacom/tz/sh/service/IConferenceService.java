package com.kedacom.tz.sh.service;

import java.util.List;

import com.kedacom.tz.sh.model.ConferenceInfoModel;
import com.kedacom.tz.sh.model.MtInfoModel;

/**
 * @see com.kedacom.tz.sh.constant.ConferenceURL
 * @author ysl
 *
 */
public interface IConferenceService {

	/**
	 * token获取
	 * 
	 * @param url                   请求url
	 * @param oauth_consumer_key    oauth中认证的软件key
	 * @param oauth_consumer_secret oauth中认证软件key对应的value
	 * @return token
	 */
	String getToken(String url, String oauth_consumer_key, String oauth_consumer_secret);

	/**
	 * 保持token心跳，获取token成功后，token有效期为30m，若30m内无操作，则需手动调用该接口保持心跳
	 * 
	 * @param url    请求url
	 * @param cookie
	 * @return
	 */
	// TODO 6.0前没有此接口
	boolean heartbeatToken(String url, List<String> cookie);

	/**
	 * 登陆
	 * 
	 * @param url      请求url
	 * @param token    软件权限token
	 * @param username 用户名
	 * @param password 密码
	 * @return cookie
	 */
	List<String> login(String url, String token, String username, String password);

	/**
	 * 保持用户心跳
	 * 
	 * 成功登陆后，用户有效期为30m，若30m内无操作，则需手动调用该接口保持心跳
	 * 
	 * @param url    请求url
	 * @param token  登陆token
	 * @param cookie
	 * @return
	 */
	boolean heartbeatUser(String url, String token, List<String> cookie);

	/**
	 * 获取登录认证API版本信息
	 * 
	 * @param url    请求url
	 * @param cookie
	 * @return 版本信息
	 */
	// TODO 6.0前不支持此接口
	String getVersion(String url, List<String> cookie);

	/**
	 * 创建会议
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 * @return confId
	 */
	String createConf(String url, String token, String param, List<String> cookie);

	/**
	 * 结束会议
	 * 
	 * @param url
	 * @param token
	 * @param cookie
	 */
	void endConf(String url, String token, List<String> cookie);

	/**
	 * 指定会议主席
	 * 
	 * 仅支持本级会议终端设置为会议主席，同步操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void putChairman(String url, String token, String param, List<String> cookie);

	/**
	 * 获取会议主席
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	String getChairman(String url, List<String> cookie);

	/**
	 * 指定会议发言人
	 * 
	 * 指定会议发言人，同步操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void putSpeaker(String url, String token, String param, List<String> cookie);

	/**
	 * 获取会议发言人
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	String getSpeaker(String url, List<String> cookie);

	/**
	 * 批量添加本级终端
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void addMts(String url, String token, String param, List<String> cookie);

	/**
	 * 批量删除终端
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void deleteMts(String url, String token, String param, List<String> cookie);

	/**
	 * 批量呼叫终端
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void onlineMts(String url, String token, String param, List<String> cookie);

	/**
	 * 批量挂断终端
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void offlineMts(String url, String token, String param, List<String> cookie);

	/**
	 * 获取视频会议信息
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	ConferenceInfoModel getConfInfo(String url, List<String> cookie);

	/**
	 * 获取与会终端信息
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	MtInfoModel getMtInfo(String url, List<String> cookie);

	/**
	 * 获取视频会议列表
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	List<ConferenceInfoModel> getConfList(String url, List<String> cookie);

	/**
	 * 获取本级会议终端列表
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	List<MtInfoModel> getMtList(String url, List<String> cookie);

	/**
	 * 指定会议双流源
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void putDualstream(String url, String token, String param, List<String> cookie);

	/**
	 * 取消会议双流源
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void deleteDualstream(String url, String token, String param, List<String> cookie);

	/**
	 * 获取会议双流源
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	String getDualstream(String url, List<String> cookie);

	/**
	 * 延长会议时间
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void delayConfTime(String url, String token, String param, List<String> cookie);

	/**
	 * 会场静音操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void confSilence(String url, String token, String param, List<String> cookie);

	/**
	 * 会场哑音操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void confMute(String url, String token, String param, List<String> cookie);

	/**
	 * 终端静音操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void confMTSilence(String url, String token, String param, List<String> cookie);

	/**
	 * 终端哑音操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void confMtMute(String url, String token, String param, List<String> cookie);

	/**
	 * 发送短消息
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	// TODO 此接口测试无效果
	void sendMessage(String url, String token, String param, List<String> cookie);

	/**
	 * 开启会议混音
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void startMix(String url, String token, String param, List<String> cookie);

	/**
	 * 停止会议混音
	 * 
	 * @param url
	 * @param token
	 * @param cookie
	 */
	void stopMix(String url, String token, List<String> cookie);

	/**
	 * 添加混音成员
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void addMixMember(String url, String token, String param, List<String> cookie);

	/**
	 * 删除混音成员
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void deleteMixMember(String url, String token, String param, List<String> cookie);

	/**
	 * 开启会议画面合成
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void startVmp(String url, String token, String param, List<String> cookie);

	/**
	 * 修改会议画面合成
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void modifyVmp(String url, String token, String param, List<String> cookie);

	/**
	 * 停止会议画面合成
	 * 
	 * @param url
	 * @param token
	 * @param cookie
	 */
	void stopVmp(String url, String token, List<String> cookie);

	/**
	 * 将监控源的RTP码流发送到指定的IP端口，监控成功后，需要发送心跳消息保持链路，同步操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void monitor(String url, String token, String param, List<String> cookie);

	/**
	 * 取消监控
	 * 
	 * @param url
	 * @param token
	 * @param cookie
	 */
	void cancelMonitor(String url, String token, List<String> cookie);

	/**
	 * 监控心跳保活
	 * 
	 * 监控成功后，若30s内没收到心跳，将自动停止发送码流，同步操作
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void heartbeatMonitor(String url, String token, String param, List<String> cookie);

	/**
	 * 监控请求视频关键帧
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void neediframeMonitor(String url, String token, String param, List<String> cookie);

	/**
	 * 修改终端音量
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void volumeControl(String url, String token, String param, List<String> cookie);

	/**
	 * 终端摄像头控制
	 * 
	 * @param url
	 * @param token
	 * @param param
	 * @param cookie
	 */
	void cameraControl(String url, String token, String param, List<String> cookie);

}
