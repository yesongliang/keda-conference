package com.kedacom.tz.sh.cometd.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cometd.IHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 终端列表
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
	}

}
