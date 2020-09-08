package com.kedacom.tz.sh.cometd.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.cometd.IHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 监控
 * 
 * @author ysl
 *
 */
@Component
@Slf4j
public class MonitorHandler implements IHandler {

	@Override
	public void handle(Map<String, String> map) {
		String platformId = map.get("key");
		String confId = map.get("conf_id");
		String method = map.get("method");
		String dstIp = map.get("dst_ip");
		String dstPort = map.get("dst_port");
		log.debug("监控推送处理,platformId={},confId={},method={},dstIp={},dstPort={}", platformId, confId, method, dstIp, dstPort);
		// TODO 业务处理待完善
	}

}
