package com.kedacom.tz.sh.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.kedacom.tz.sh.cache.PlatformBaseData;
import com.kedacom.tz.sh.cache.PlatformExtendData;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.service.IBusinessService;
import com.kedacom.tz.sh.thread.ConferencePlatform;
import com.kedacom.tz.sh.utils.ConferencePlatformManager;

@Service
public class BusinessService implements IBusinessService {

	@Autowired
	private Environment environment;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public PlatformVo getPlatformByKey(Long key) {
		PlatformVo vo = null;
		// 是否使用分布式||集群
		Boolean distributed = environment.getProperty("distribute.enable", Boolean.class, false);
		// 分布式||集群
		if (distributed) {
			// 获取缓存基础数据
			Object object = redisTemplate.opsForValue().get("platform_base_data:" + key);
			if (Objects.nonNull(object)) {
				PlatformBaseData platform = (PlatformBaseData) object;
				vo = new PlatformVo();
				vo.setId(key);
				vo.setIp(platform.getIp());
				vo.setPort(platform.getPort());
				vo.setOauth_consumer_key(platform.getOauth_consumer_key());
				vo.setOauth_consumer_secret(platform.getOauth_consumer_secret());
				vo.setUsername(platform.getUsername());
				vo.setPassword(platform.getPassword());
				object = redisTemplate.opsForValue().get("platform_extend_data:" + key);
				if (Objects.nonNull(object)) {
					PlatformExtendData platformExtendData = (PlatformExtendData) object;
					vo.setConnected(platformExtendData.isConnected());
					vo.setLogin(platformExtendData.isIslogin());
					vo.setToken(platformExtendData.getToken());
					vo.setCookie(platformExtendData.getCookie());
				} else {
					vo.setConnected(false);
					vo.setLogin(false);
				}
			}
		} else {
			ConferencePlatform platform = ConferencePlatformManager.getPlatform(key);
			if (platform != null) {
				vo = new PlatformVo(key, platform.getIp(), platform.getPort(), platform.getOauth_consumer_key(), platform.getOauth_consumer_secret(), platform.getUsername(), platform.getPassword(),
						platform.isIslogin(), platform.getClient() == null ? false : platform.getClient().isConnected(), platform.getToken(), platform.getCookie());
			}
		}
		return vo;
	}

}
