package com.kedacom.tz.sh.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kedacom.tz.sh.cache.PlatformBaseData;
import com.kedacom.tz.sh.cache.PlatformExtendData;
import com.kedacom.tz.sh.cache.RedisCacheConstant;
import com.kedacom.tz.sh.constant.ConferenceConstant;
import com.kedacom.tz.sh.controller.request.PlatformParam;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.service.IBusinessService;
import com.kedacom.tz.sh.thread.ConferencePlatform;
import com.kedacom.tz.sh.utils.ConferencePlatformManager;
import com.kedacom.tz.sh.utils.ConferencePlatformManager.AddType;
import com.kedacom.tz.sh.utils.IPUtils;
import com.kedacom.tz.sh.utils.RedisLock;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "多会议平台管理类接口")
public class ManagerController {

	@Autowired
	private Environment environment;
	@Autowired
	private RedisLock redisLock;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private Executor executor;
	@Autowired
	private IBusinessService businessService;

	@PostMapping("add/platform")
	@ApiOperation(value = "添加会议平台")
	public Long addPlatform(@RequestBody @Validated PlatformParam param) {
		long ipToLong = IPUtils.ipToLong(param.getIp());
		ConferencePlatform conferencePlatform = new ConferencePlatform(param.getIp(), param.getPort(), param.getOauth_consumer_key(), param.getOauth_consumer_secret(), param.getUsername(),
				param.getPassword());
		// 是否使用分布式||集群
		Boolean distributed = environment.getProperty(ConferenceConstant.DISTRIBUTE_ENABLE, Boolean.class, false);
		// 分布式||集群
		if (distributed) {
			Object object = redisTemplate.opsForValue().get(RedisCacheConstant.PLATFORM_BASE_DATA_PRE + ipToLong);
			if (Objects.nonNull(object)) {
				throw new BusinessException("不允许重复添加会议平台");
			}
			Integer capacity = environment.getProperty(ConferenceConstant.DISTRIBUTE_CAPACITY, Integer.class, 32);
			String fetchLockValue = redisLock.fetchLockValue();
			PlatformBaseData platform = new PlatformBaseData();
			platform.transform(conferencePlatform);
			// 加锁
			if (redisLock.lock("add_platform", fetchLockValue)) {
				try {
					Set<String> keys = redisTemplate.keys(RedisCacheConstant.PLATFORM_BASE_DATA_PRE + RedisCacheConstant.PATTERN_ALL);
					if (keys.size() < capacity) {
						redisTemplate.opsForValue().set(RedisCacheConstant.PLATFORM_BASE_DATA_PRE + ipToLong, platform, 60, TimeUnit.SECONDS);
					} else {
						throw new BusinessException("会议平台数量已达上限:" + capacity);
					}
				} finally {
					redisLock.unLock("add_platform", fetchLockValue);
				}
				executor.execute(conferencePlatform);
			} else {
				throw new BusinessException("请稍后再试...");
			}
			// 单机
		} else {
			boolean exist = ConferencePlatformManager.isExist(ipToLong);
			if (exist) {
				throw new BusinessException("不允许重复添加会议平台");
			}
			// 需加锁
			AddType addPlatform = ConferencePlatformManager.addPlatform(ipToLong, conferencePlatform);
			switch (addPlatform) {
			case EXIST:
				throw new BusinessException("不允许重复添加ip相同的会议平台");
			case OUTOFSIZE:
				throw new BusinessException("会议平台数量已达上限:" + ConferencePlatformManager.getCapacity());
			default:
				break;
			}
		}
		return ipToLong;
	}

	@GetMapping("get/platforms")
	@ApiOperation(value = "获取所有会议平台")
	public List<PlatformVo> getPlatforms() {
		List<PlatformVo> list = new ArrayList<>();
		// 是否使用分布式||集群
		Boolean distributed = environment.getProperty(ConferenceConstant.DISTRIBUTE_ENABLE, Boolean.class, false);
		// 分布式||集群
		if (distributed) {
			Set<String> keys = redisTemplate.keys(RedisCacheConstant.PLATFORM_BASE_DATA_PRE + RedisCacheConstant.PATTERN_ALL);
			for (String key : keys) {
				PlatformVo vo = null;
				// 获取缓存基础数据
				Object object = redisTemplate.opsForValue().get(key);
				if (Objects.nonNull(object)) {
					PlatformBaseData platform = (PlatformBaseData) object;
					vo = new PlatformVo();
					long platformKey = IPUtils.ipToLong(platform.getIp());
					vo.setId(platformKey);
					vo.setIp(platform.getIp());
					vo.setPort(platform.getPort());
					vo.setOauth_consumer_key(platform.getOauth_consumer_key());
					vo.setOauth_consumer_secret(platform.getOauth_consumer_secret());
					vo.setUsername(platform.getUsername());
					vo.setPassword(platform.getPassword());
					object = redisTemplate.opsForValue().get(RedisCacheConstant.PLATFORM_EXTEND_DATA_PRE + platformKey);
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
				if (vo != null) {
					list.add(vo);
				}
			}
		} else {
			Collection<ConferencePlatform> platforms = ConferencePlatformManager.getPlatforms();
			for (ConferencePlatform platform : platforms) {
				PlatformVo vo = new PlatformVo(IPUtils.ipToLong(platform.getIp()), platform.getIp(), platform.getPort(), platform.getOauth_consumer_key(), platform.getOauth_consumer_secret(),
						platform.getUsername(), platform.getPassword(), platform.isIslogin(), platform.getClient() == null ? false : platform.getClient().isConnected(), platform.getToken(),
						platform.getCookie());
				list.add(vo);
			}
		}
		return list;
	}

	@GetMapping("get/platform/{key}")
	@ApiOperation(value = "根据key获取会议平台")
	public PlatformVo getPlatformByKey(@PathVariable("key") Long key) {
		PlatformVo platform = businessService.getPlatformByKey(key);
		return platform;
	}

	@DeleteMapping("delete/platform")
	@ApiOperation(value = "根据key移除会议平台")
	public void removePlatformByKey(Long key) {
		// 是否使用分布式||集群
		Boolean distributed = environment.getProperty(ConferenceConstant.DISTRIBUTE_ENABLE, Boolean.class, false);
		// 分布式||集群
		if (distributed) {
			// 获取缓存基础数据
			Object object = redisTemplate.opsForValue().get(RedisCacheConstant.PLATFORM_BASE_DATA_PRE + key);
			if (Objects.nonNull(object)) {
				PlatformBaseData platform = (PlatformBaseData) object;
				platform.setRunning(false);
				redisTemplate.opsForValue().setIfPresent(RedisCacheConstant.PLATFORM_BASE_DATA_PRE + key, platform, 60, TimeUnit.SECONDS);
			}
		} else {
			// 移除
			ConferencePlatformManager.removePlatformForOut(key);
		}
	}

}
