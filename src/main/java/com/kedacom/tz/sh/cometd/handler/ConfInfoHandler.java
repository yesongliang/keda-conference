package com.kedacom.tz.sh.cometd.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cometd.IHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 会议信息
 * 
 * @author ysl
 *
 */
@Component
@Slf4j
public class ConfInfoHandler implements IHandler {

	@Override
	public void handle(Map<String, String> map) {
		String platformId = map.get("key");
		String confId = map.get("conf_id");
		String method = map.get("method");
		log.debug("会议信息推送处理,platformId={},confId={},method={}", platformId, confId, method);
		// TODO 业务处理待完善
	}

}
