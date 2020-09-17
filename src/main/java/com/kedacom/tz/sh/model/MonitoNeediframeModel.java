package com.kedacom.tz.sh.model;

import lombok.Data;

/**
 * 监控请求关键帧
 * 
 * @author ysl
 *
 */
@Data
public class MonitoNeediframeModel {

	/** 目的地址 **/
	private Dst dst;

	@Data
	private class Dst {
		/** 目的ip地址 **/
		private String ip;
		/** 目的rtp端口 **/
		private Integer port;
	}

	public void setDst(String ip, Integer port) {
		Dst dst = new Dst();
		dst.setIp(ip);
		dst.setPort(port);
		this.dst = dst;
	}
}
