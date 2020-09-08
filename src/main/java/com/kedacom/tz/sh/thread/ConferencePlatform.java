package com.kedacom.tz.sh.thread;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import com.kedacom.tz.sh.cache.PlatformBaseData;
import com.kedacom.tz.sh.cache.PlatformExtendData;
import com.kedacom.tz.sh.cometd.CometDClient;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.service.IConferenceService;
import com.kedacom.tz.sh.service.impl.ConferenceServiceImpl;
import com.kedacom.tz.sh.utils.ConferencePlatformManager;
import com.kedacom.tz.sh.utils.IPUtils;
import com.kedacom.tz.sh.utils.SpringBeanUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ConferencePlatform implements Runnable {

	/** 会议平台地址 **/
	private String ip;
	/** 会议平台端口 **/
	private Integer port = 80;
	/** oauth中认证的软件key **/
	private String oauth_consumer_key;
	/** oauth中认证软件key对应的value **/
	private String oauth_consumer_secret;
	/** 用户名 **/
	private String username;
	/** 密码 **/
	private String password;

	/** 登录是否成功 **/
	private boolean islogin = false;
	/** 心跳连续失败次数 **/
	private int fail_num = 0;
	/** 心跳计数器 **/
	private int heartbeat_count = 0;
	/** 控制线程运行停止 **/
	private boolean running = true;
	/** CometD客户端 **/
	private CometDClient client;

	/** 接口获取到的token **/
	private String token;
	/** 登录成功返回的cookie **/
	private List<String> cookie;

	public ConferencePlatform(String ip, String oauth_consumer_key, String oauth_consumer_secret, String username, String password) {
		this.ip = ip;
		this.oauth_consumer_key = oauth_consumer_key;
		this.oauth_consumer_secret = oauth_consumer_secret;
		this.username = username;
		this.password = password;
	}

	public ConferencePlatform(String ip, Integer port, String oauth_consumer_key, String oauth_consumer_secret, String username, String password) {
		this.ip = ip;
		this.port = port;
		this.oauth_consumer_key = oauth_consumer_key;
		this.oauth_consumer_secret = oauth_consumer_secret;
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		// 服务接口类
		IConferenceService conferenceService = SpringBeanUtils.getBeanByClass(ConferenceServiceImpl.class);
		// TODO 是否使用分布式缓存？
		RedisTemplate<String, Object> redisTemplate = SpringBeanUtils.getBean("redisTemplate");
		Environment environment = SpringBeanUtils.getBeanByClass(Environment.class);
		// 是否使用分布式缓存
		Boolean distributed = environment.getProperty("distribute.enable", Boolean.class, false);
		long ipToLong = IPUtils.ipToLong(this.ip);
		while (this.running) {

			try {

				if (!this.islogin) {

					// TODO 写在死循环开始，登录成功后慢一个循环周期才能使用；写在死循环结束，出现异常会导致缓存数据失效，线程运行停止
					if (distributed) {
						// 获取缓存基础数据
						Object object = redisTemplate.opsForValue().get("platform_base_data:" + ipToLong);
						if (Objects.nonNull(object)) {
							PlatformBaseData platform = (PlatformBaseData) object;
							if (platform.isRunning()) {
								// 更新基础数据过期时间
								redisTemplate.expire("platform_base_data:" + ipToLong, 60, TimeUnit.SECONDS);
								// 更新扩展数据缓存
								PlatformExtendData platformExtendData = new PlatformExtendData();
								platformExtendData.transform(this);
								redisTemplate.opsForValue().set("platform_extend_data:" + ipToLong, platformExtendData, 60, TimeUnit.SECONDS);
							} else {
								// 线程运行结束
								// 删除缓存
								redisTemplate.delete("platform_base_data:" + ipToLong);
								redisTemplate.delete("platform_extend_data:" + ipToLong);
								break;
							}
						} else {
							// 缓存过期，线程结束
							break;
						}
					}

					// 1、获取token
					String tokenUrl = String.format(ConferenceURL.TOKEN.getUrl(), this.ip, this.port);
					this.token = conferenceService.getToken(tokenUrl, this.oauth_consumer_key, this.oauth_consumer_secret);
					if (this.token != null) {
						// 2、登录
						String loginUrl = String.format(ConferenceURL.LOGIN.getUrl(), this.ip, this.port);
						this.cookie = conferenceService.login(loginUrl, token, this.username, this.password);
						log.debug("cookie={}", this.cookie);
						if (!CollectionUtils.isEmpty(this.cookie)) {
							// 3、cometD初始化
							// TODO 重复获取的token是否一致，重复登录返回的cookie是否一致？
							if (this.client != null && this.client.isConnected()) {
								this.client.disconnect();
							}
							this.client = new CometDClient(this.ip, this.port, this.cookie);
							this.client.handShake();

							this.islogin = true;
						}
					}
				}

				// cometD连接成功后，会维护连接，是否就不用再进行用户保活？（经产品线确认，还须保活用户心跳）
				if (this.islogin) {

					// TODO 写在死循环开始，登录成功后慢一个循环周期才能使用；写在死循环结束，出现异常会导致缓存数据失效，线程运行停止
					if (distributed) {
						// 获取缓存基础数据
						Object object = redisTemplate.opsForValue().get("platform_base_data:" + ipToLong);
						if (Objects.nonNull(object)) {
							PlatformBaseData platform = (PlatformBaseData) object;
							if (platform.isRunning()) {
								// 更新基础数据过期时间
								redisTemplate.expire("platform_base_data:" + ipToLong, 60, TimeUnit.SECONDS);
								// 更新扩展数据缓存
								PlatformExtendData platformExtendData = new PlatformExtendData();
								platformExtendData.transform(this);
								redisTemplate.opsForValue().set("platform_extend_data:" + ipToLong, platformExtendData, 60, TimeUnit.SECONDS);
							} else {
								// 线程运行结束
								// 删除缓存
								redisTemplate.delete("platform_base_data:" + ipToLong);
								redisTemplate.delete("platform_extend_data:" + ipToLong);
								break;
							}
						} else {
							// 缓存过期，线程结束
							break;
						}
					}

					// TODO cometD可认为是长连接，是否可用cometD的连接状态判断，在网络震荡的情况下如何优化?
					this.heartbeat_count++;
					// 有效期为30分钟，设计为间隔5分钟一次心跳
					if (this.heartbeat_count > 30) {
						// 4、保持用户心跳
						String heartbeatUserUrl = String.format(ConferenceURL.HEARTBEAT_USER.getUrl(), this.ip, this.port);
						boolean heartbeatUser = conferenceService.heartbeatUser(heartbeatUserUrl, this.token, this.cookie);
						if (heartbeatUser) {
							this.fail_num = 0;
							this.heartbeat_count = 1;
						} else {
							this.fail_num++;
							// 3次
							if (this.fail_num > 2) {
								this.islogin = false;
								this.fail_num = 0;
								this.heartbeat_count = 0;
							}
						}
					}
				}

			} catch (Exception e) {
//				log.error("会议平台维护线程业务异常,msg={}", e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
//					log.error("会议平台维护线程执行异常,msg={}", e.getMessage());
					e.printStackTrace();
				}
			}
		}
		// 跳出死循环，执行线程退出前操作

		// 1.断开cometD连接
		if (this.client != null && this.client.isConnected()) {
			this.client.disconnect();
		}

		// 2.移除，后续可加消息队列实现异步推送
		if (!distributed) {
			ConferencePlatformManager.removePlatformForIn(ipToLong);
		}

		log.debug("会议平台维护线程运行结束,ip={}", this.ip);
	}

}
