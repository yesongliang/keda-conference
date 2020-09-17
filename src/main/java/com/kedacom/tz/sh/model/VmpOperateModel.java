package com.kedacom.tz.sh.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 会议画面合成
 * 
 * @author ysl
 *
 */
@Data
public class VmpOperateModel {

	/*** 画面合成模式 1-定制画面合成； 2-自动画面合成； 3-自动画面合成批量轮询； 4-定制画面合成批量轮询； **/
	private Integer mode;

	/***
	 * 画面合成风格: 1-一画面全屏; 2-两画面: 2等大，居中(1行2列); 3-两画面: 1大1小，1大全屏，1小右下; 61-两画面:
	 * 1大1小，1大全屏，1小右上; 62-两画面: 1大1小，1大全屏，1小左上; 63-两画面: 1大1小，1大全屏，1小左下; 23-三画面:
	 * 等大，1左，2右(2行1列); 4-三画面: 等大，1上2下; 5-四画面: 等大，2行2列; 35-五画面: 1大4小，1大上，4小下(1行4列);
	 * 6-六画面: 1大5小，1大左上，2小右上(2行1列)，3小下(1行3列); 13-七画面:
	 * 3大4小，2大上(1行2列)，1大左下，4小右下(2行2列); 7-八画面: 1大7小，1大左上，3小右上(3行1列)，4小下(1行4列); 8-九画面:
	 * 等大，3行3列; 18-十画面: 2大8小，4小上(1行4列)，2大中(1行2列)，4小下(1行4列); 38-十一画面:
	 * 1大10小，1大上，10小下(2行5列); 39-十二画面: 3大9小，2大上(1行2列)，1大左下，9小右下(3行3列); 19-十三画面:
	 * 1大12小，4小上(1行4列)，2小左中(2行1列)，1大中中，2小右中(2行1列), 4小下(1行4列); 17-十四画面:
	 * 2大12小，2大左上(1行2列)，2小右上(2行1列)，10小下(2行5列); 20-十五画面: 3大12小，3大上(1行3列)，12小下(2行6列);
	 * 11-十六画面: 16等分，4x4; 46-十七画面: 1大16小，1大左上，6小右上(3行2列)，10小下(2行5列); 48-十八画面:
	 * 6大12小，6小上(1行6列)，6大居中(2行3列)，6小下(1行6列); 51-十九画面:
	 * 2大17小，2大左上(1行2列)，2小右上(2行1列)，15小下(3行5列); 14-二十画面: 2大18小，2大上(1行2列)，18小下(3行6列);
	 * 54-二十一画面: 1大20小，6小上(1行6列)，4小左中(4行1列)，1大中中，4小右中(4行1列)，6小下(1行6列); 56-二十二画面:
	 * 1大21小，1大左上，6小右上(2行3列)，15小下(3行5列); 59-二十四画面:
	 * 4大20小，6小上(1行6列)，4小左中(4行1列)，4大中中(2行2列)，4小右中(4行1列)，6小下(1行6列)； 27-二十五画面:
	 * 等大，5行5列;
	 **/
	private Integer layout;

	/** 是否广播 0-否； 1-是； **/
	private Integer broadcast = 1;

	/** 是否识别声音来源 0-否； 1-是； **/
	private Integer voice_hint = 1;

	/** 是否显示别名 0-否； 1-是； **/
	private Integer show_mt_name = 1;

	/** 画面合成台标参数 **/
	private MtNameStyle mt_name_style = new MtNameStyle();

	/** 画面合成成员数组 **/
	private List<Member> members;

	/** 画面合成批量轮询信息,仅mode为3、4时有效 **/
	private Poll poll;

	@Data
	private class MtNameStyle {
		/** 台标字体大小, 默认为: 0 0-小； 1-中； 2-大； **/
		private Integer font_size = 0;
		/** 台标字体三原色#RGB格式, 十六进制表示, 默认为: #FFFFFF白色 **/
		private String font_color = "#FFFFFF";
		/** 台标显示位置, 默认为: 1 0-左上角； 1-左下角； 2-右上角； 3-右下角； 4-底部中间； **/
		private Integer position = 1;
	}

	@Data
	private class Member {
		/** 画面合成通道索引，从0开始 **/
		private Integer chn_idx;
		/** 成员类型 1-会控指定； 2-发言人跟随； 3-主席跟随； 4-轮询跟随； 6-单通道轮询； 7-双流跟随； **/
		private Integer member_type = 1;
		/** 通道终端号，仅当通道中为会控指定时需要 最大字符长度：48个字节 **/
		private String mt_id;
		/** 终端视频通道号，仅成员类型为会控指定时有效，0为自动选择，默认值为0 **/
		private Integer mt_chn_idx = 0;
		/** 单通道轮询参数，仅member_type为6时有效 **/
		private Poll poll;
	}

	@Data
	private class Poll {
		/** 画面合成单通道轮询次数 **/
		private Integer num;
		/** 画面合成单通道轮询间隔时间,单位：秒 **/
		private Integer keep_time;
		/** 开始轮询的终端顺序索引，默认为1 **/
		private Integer poll_index;
		/** 单通道轮询成员数组 **/
		private List<Mt> members;
	}

	@Data
	private class Mt {
		/** 成员终端号 最大字符长度：48个字节 **/
		private String mt_id;
	}

	public void addMember(Integer chnIdx, String mtId) {
		if (this.members == null) {
			this.members = new ArrayList<>();
		}
		Member member = new Member();
		member.setChn_idx(chnIdx);
		member.setMt_id(mtId);
		this.members.add(member);
	}

}
