package com.kedacom.tz.sh.cache;

import java.util.List;

import com.kedacom.tz.sh.thread.ConferencePlatform;

import lombok.Data;

/**
 * 平台扩展数据缓存
 * 
 * 60秒过期，平台维护线程每10秒更新一次过期时间和数据
 * 
 * @author ysl
 *
 */
@Data
public class PlatformExtendData {
	/** 登录是否成功 **/
	private boolean islogin;
	/** cometD连接否成功 **/
	private boolean isConnected;
	/** 接口获取到的token **/
	private String token;
	/** 登录成功返回的cookie **/
	private List<String> cookie;

	public void transform(ConferencePlatform conferencePlatform) {
		this.islogin = conferencePlatform.isIslogin();
		this.isConnected = conferencePlatform.getClient() == null ? false : conferencePlatform.getClient().isConnected();
		this.token = conferencePlatform.getToken();
		this.cookie = conferencePlatform.getCookie();
	}
}
