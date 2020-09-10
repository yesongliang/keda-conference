package com.kedacom.tz.sh.cometd.handler;

import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cache.RedisCacheConstant;
import com.kedacom.tz.sh.cometd.IHandler;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.model.MtInfoModel;
import com.kedacom.tz.sh.service.IBusinessService;
import com.kedacom.tz.sh.service.IConferenceService;
import com.kedacom.tz.sh.utils.SpringBeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 终端列表(cascade_id=0,代表本级会议)
 * 
 * @author ysl
 *
 */
@Component
@Slf4j
public class MtListHandler implements IHandler {

	@Override
	public void handle(Map<String, String> map) {
		String platformId = map.get("key");
		String confId = map.get("conf_id");
		String mtId = map.get("mt_id");
		String method = map.get("method");
		log.debug("终端列表推送处理,platformId={},confId={},mtId={},method={}", platformId, confId, mtId, method);
		// TODO 业务处理待完善
		IBusinessService businessService = SpringBeanUtils.getBeanByClass(IBusinessService.class);
		IConferenceService conferenceService = SpringBeanUtils.getBeanByClass(IConferenceService.class);
		RedisTemplate<String, Object> redisTemplate = SpringBeanUtils.getBean("redisTemplate");
		if ("update".equals(method)) {
			PlatformVo platform = businessService.getPlatformByKey(Long.parseLong(platformId));
			if (platform == null || !platform.isLogin() || !platform.isConnected()) {
				log.error("会议平台暂不可用,丢弃消息:{}", map);
				return;
			}
			// 获取与会终端信息
			String url = String.format(ConferenceURL.GET_CONF_MT_INFO.getUrl(), platform.getIp(), platform.getPort(), confId, mtId, platform.getToken());
			MtInfoModel mtInfo = conferenceService.getMtInfo(url, platform.getCookie());
			redisTemplate.opsForHash().put(RedisCacheConstant.MT_INFO_PRE + platformId + ":" + confId, mtId, mtInfo);
		} else if ("delete".equals(method)) {
			redisTemplate.opsForHash().delete(RedisCacheConstant.MT_INFO_PRE + platformId + ":" + confId, mtId);
		}
	}

}
