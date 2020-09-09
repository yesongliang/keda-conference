package com.kedacom.tz.sh.service;

import java.util.List;

import com.kedacom.tz.sh.model.ConferenceInfoModel;
import com.kedacom.tz.sh.model.MtInfoModel;

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
	 * 保持token心跳
	 * 
	 * 获取token成功后，token有效期为30m，若30m内无操作，则需手动调用该接口保持心跳
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
	// TODO 6.0前没有此接口
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
	 * @param token
	 * @param cookie
	 * @return
	 */
	String getChairman(String url, String token, List<String> cookie);

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
	 * @param token
	 * @param cookie
	 * @return
	 */
	ConferenceInfoModel getConfInfo(String url, List<String> cookie);

	/**
	 * 获取与会终端信息
	 * 
	 * @param url
	 * @param token
	 * @param cookie
	 * @return
	 */
	MtInfoModel getMtInfo(String url, List<String> cookie);

}
