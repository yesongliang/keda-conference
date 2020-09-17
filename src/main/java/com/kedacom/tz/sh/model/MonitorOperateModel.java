package com.kedacom.tz.sh.model;

import lombok.Data;

/**
 * 监控操作
 * 
 * @author ysl
 *
 */
@Data
public class MonitorOperateModel {
	/** 监控模式 0-视频； 1-音频； **/
	private Integer mode;
	/** 监控源 **/
	private Src src;
	/** 仅监控画面合成时有效 **/
	private VideoFormat video_format;
	/** 仅监控混音时有效 **/
	private AudioFormat audio_format;
	/** 目的地址 **/
	private Dst dst;

	@Data
	private class Src {
		/** 监控类型 1-终端； 2-画面合成； 3-混音； 4-双流； **/
		private Integer type = 1;
		/** 监控终端id, type为终端时必填 最大字符长度：48个字节 **/
		private String mt_id;
		/** 终端通道，type为终端时有效，默认为1 **/
		private Integer mt_chn_idx = 1;
	}

	@Data
	private class VideoFormat {
		/**
		 * 主视频格式 1-MPEG; 2-H.261; 3-H.263; 4-H.264_HP; 5-H.264_BP; 6-H.265; 7-H.263+;
		 **/
		private Integer format;
		/**
		 * 主视频分辨率 1-QCIF; 2-CIF; 3-4CIF; 12-720P; 13-1080P; 14-WCIF; 15-W4CIF; 16-4k;
		 **/
		private Integer resolution;
		/** 主视频帧率 **/
		private Integer frame;
		/** 主视频码率 **/
		private Integer bitrate;
	}

	@Data
	private class AudioFormat {
		/**
		 * 音频类型 1-MP3; 2-G722.1.C; 3-G719; 4-G.711(Alaw); 5-G.711(Ulaw); 6-G.721;
		 * 7-G.722; 8-G.7231; 9-G.728; 10-G.729; 11-G.7221; 12-OPUS; 13-MPEG-4 AAC LC;
		 * 14-MPEG-4 AAC LD;
		 **/
		private Integer format;
		/**
		 * 声轨数量 1-单声道； 2-双声道；
		 **/
		private Integer chn_num;
	}

	@Data
	private class Dst {
		/** 目的ip地址 **/
		private String ip;
		/** 目的rtp端口 **/
		private Integer port;
	}

	public void setSrc(String mtId) {
		Src src = new Src();
		src.setMt_id(mtId);
		this.src = src;
	}

	public void setDst(String ip, Integer port) {
		Dst dst = new Dst();
		dst.setIp(ip);
		dst.setPort(port);
		this.dst = dst;
	}
}
