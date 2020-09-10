package com.kedacom.tz.sh.cometd.handler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cache.RedisCacheConstant;
import com.kedacom.tz.sh.cometd.IHandler;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.model.ConferenceInfoModel;
import com.kedacom.tz.sh.model.MtInfoModel;
import com.kedacom.tz.sh.service.IBusinessService;
import com.kedacom.tz.sh.service.IConferenceService;
import com.kedacom.tz.sh.utils.SpringBeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 订阅
 * 
 * @author ysl
 *
 */
@Component
@Slf4j
public class InitHandler implements IHandler {

	@Override
	public void handle(Map<String, String> map) {
		String platformId = map.get("key");
		log.debug("订阅推送处理,platformId={}", platformId);
		// TODO 同步会议平台上的会议：1、获取视频会议列表；2、获取与会终端列表
		IBusinessService businessService = SpringBeanUtils.getBeanByClass(IBusinessService.class);
		IConferenceService conferenceService = SpringBeanUtils.getBeanByClass(IConferenceService.class);
		RedisTemplate<String, Object> redisTemplate = SpringBeanUtils.getBean("redisTemplate");

		PlatformVo platform = businessService.getPlatformByKey(Long.parseLong(platformId));
		if (platform == null || !platform.isLogin() /* || !platform.isConnected() */) {
			log.error("会议平台暂不可用,丢弃消息:{}", map);
			return;
		}
		// 清理缓存
		Set<String> conf_keys = redisTemplate.keys(RedisCacheConstant.CONF_INFO_PRE + platformId + ":*");
		redisTemplate.delete(conf_keys);
		Set<String> mt_keys = redisTemplate.keys(RedisCacheConstant.MT_INFO_PRE + platformId + ":*");
		redisTemplate.delete(mt_keys);

		// 获取视频会议列表
		String conf_list_url = String.format(ConferenceURL.GET_CONF_LIST.getUrl(), platform.getIp(), platform.getPort(), platform.getToken());
		List<ConferenceInfoModel> confList = conferenceService.getConfList(conf_list_url, platform.getCookie());
		for (ConferenceInfoModel conferenceInfoModel : confList) {
			redisTemplate.opsForValue().set(RedisCacheConstant.CONF_INFO_PRE + platformId + ":" + conferenceInfoModel.getConf_id(), conferenceInfoModel);
			// 获取与会终端列表
			String mt_list_url = String.format(ConferenceURL.GET_CONF_MT_LIST.getUrl(), platform.getIp(), platform.getPort(), conferenceInfoModel.getConf_id(), platform.getToken());
			List<MtInfoModel> mtList = conferenceService.getMtList(mt_list_url, platform.getCookie());
			for (MtInfoModel mtInfoModel : mtList) {
				redisTemplate.opsForHash().put(RedisCacheConstant.MT_INFO_PRE + platformId + ":" + conferenceInfoModel.getConf_id(), mtInfoModel.getMt_id(), mtInfoModel);
			}
		}
	}

}
