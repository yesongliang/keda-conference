package com.kedacom.tz.sh.cometd.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cometd.IHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 画面合成
 * 
 * @author ysl
 *
 */
@Component
@Slf4j
public class VmpHandler implements IHandler {

	@Override
	public void handle(Map<String, String> map) {
		String platformId = map.get("key");
		String confId = map.get("conf_id");
		String vmpId = map.get("vmp_id");
		String method = map.get("method");
		log.debug("画面合成推送处理,platformId={},confId={},vmpId={},method={}", platformId, confId, vmpId, method);
		// TODO 业务处理待完善
	}

}
