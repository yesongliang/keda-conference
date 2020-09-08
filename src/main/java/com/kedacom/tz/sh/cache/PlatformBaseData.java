package com.kedacom.tz.sh.cache;

import com.kedacom.tz.sh.thread.ConferencePlatform;

import lombok.Data;

/**
 * 平台基础数据缓存
 * 
 * 60秒过期，平台维护线程每10秒更新一次过期时间
 * 
 * @author ysl
 *
 */
@Data
public class PlatformBaseData {
	/** 会议平台地址 **/
	private String ip;
	/** 会议平台端口 **/
	private Integer port;
	/** oauth中认证的软件key **/
	private String oauth_consumer_key;
	/** oauth中认证软件key对应的value **/
	private String oauth_consumer_secret;
	/** 用户名 **/
	private String username;
	/** 密码 **/
	private String password;
	/** 控制线程运行停止 **/
	private boolean running;

	public void transform(ConferencePlatform conferencePlatform) {
		this.ip = conferencePlatform.getIp();
		this.port = conferencePlatform.getPort();
		this.oauth_consumer_key = conferencePlatform.getOauth_consumer_key();
		this.oauth_consumer_secret = conferencePlatform.getOauth_consumer_secret();
		this.username = conferencePlatform.getUsername();
		this.password = conferencePlatform.getPassword();
		this.running = conferencePlatform.isRunning();
	}
}
