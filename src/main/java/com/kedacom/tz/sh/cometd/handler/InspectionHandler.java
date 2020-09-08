package com.kedacom.tz.sh.cometd.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cometd.IHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 终端选看
 * 
 * @author ysl
 *
 */
@Component
@Slf4j
public class InspectionHandler implements IHandler {

	@Override
	public void handle(Map<String, String> map) {
		String platformId = map.get("key");
		String confId = map.get("conf_id");
		String method = map.get("method");
		String mtId = map.get("mt_id");
		String mode = map.get("mode");
		log.debug("终端选看推送处理,platformId={},confId={},method={},mtId={},mode={}", platformId, confId, method, mtId, mode);
		// TODO 业务处理待完善
	}

}
