package com.kedacom.tz.sh.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 监控心跳保活
 * 
 * @author ysl
 *
 */
@Data
public class MonitorHeartbeatModel {

	/** 该消息需要每10秒发送一次，若30秒未收到，则监控自动停止 **/
	private List<Member> monitors;

	@Data
	private class Member {
		/** 目的ip地址 **/
		private String ip;
		/** 目的rtp端口 **/
		private Integer port;
	}

	public void addMember(String ip, Integer port) {
		if (this.monitors == null) {
			this.monitors = new ArrayList<>();
		}
		Member member = new Member();
		member.setIp(ip);
		member.setPort(port);
		this.monitors.add(member);
	}

}
