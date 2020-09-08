package com.kedacom.tz.sh.model;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 创建会议
 * 
 * @author ysl
 *
 */
@Component
@ConfigurationProperties(prefix = "default-create-conference-param")
@Data
public class CreateConferenceModel {
	/**
	 * （必填） 创建会议类型，默认为1 1-及时会议； 2-公共模板； 3-个人模板； 4-根据虚拟会议室创会；
	 **/
	private Integer create_type = 1;
	/** （必填） 当creat_type不为1时必填，分别对应模板id和虚拟会议室id **/
	private String template_id;
	/**
	 * （必填） 会议名称 创建虚拟会议室时可填 1.字符限制： a.不支持输入特殊字符：% & * ^ ~ ' " " ? / \ < > | ` " $
	 * b.且首字符和尾字符不支持输入，下划线（_） 减号（-） 小数点（.） @ （除首尾字符可以输入） 2.最大字符长度：64个字节
	 **/
	private String name;
	/** （必填）会议时长 创建虚拟会议室时可填，0为永久会议 **/
	private Integer duration;
	/** （必填）会议码率，目前以主视频格式中的码率为准 **/
	private Integer bitrate;
	/**
	 * （必填）会议免打扰 0-关闭； 1-开启；
	 **/
	private Integer closed_conf;
	/**
	 * （必填）会议安全 0-公开会议； 1-隐藏会议；
	 **/
	private Integer safe_conf;
	/**
	 * 会议密码 1.字符限制：仅支持 英文字母(大小写) 数字 下划线（_） 小数点（.） 2.最大字符长度：16个字节
	 **/
	private String password;
	/**
	 * （必填）传输加密类型 0-不加密； 2-AES加密； 3-商密（SM4）； 4-商密（SM1）；
	 **/
	private Integer encrypted_type;
	/**
	 * 终端双向认证，默认为0 0-关闭； 1-开启；
	 **/
	private Integer encrypted_auth = 0;
	/**
	 * （必填） 会议类型 0-传统会议； 1-端口会议；
	 **/
	private Integer conf_type;
	/**
	 * （必填）呼叫模式 0-手动； 2-自动；
	 **/
	private Integer call_mode;
	/** （必填）呼叫次数 **/
	private Integer call_times;
	/** （必填）呼叫间隔(秒) **/
	private Integer call_interval;
	/**
	 * FEC开关，默认为0 0-关闭； 1-开启；
	 **/
	private Integer fec_mode = 0;
	/**
	 * 是否开启全场哑音例外，默认为0 0-不开启； 1-开启；
	 **/
	private Integer mute_filter = 0;
	/**
	 * （必填）是否开启初始化哑音 0-否； 1-是；
	 **/
	private Integer mute;
	/**
	 * （必填）是否开启初始化静音 0-否； 1-是；
	 **/
	private Integer silence;
	/**
	 * （必填）视频质量,其中租赁环境默认设为速度优先，自建环境默认为质量优先 0-质量优先； 1-速度优先；
	 **/
	private Integer video_quality;
	/**
	 * （必填）传输加密AES加密密钥 1.字符限制：仅支持 英文字母(大小写) 数字 下划线（_） 小数点（.） 2.最大字符长度：16个字节
	 **/
	private String encrypted_key;
	/**
	 * （必填）双流权限 0-发言会场； 1-任意会场；
	 **/
	private Integer dual_mode;
	/**
	 * 成为发言人后立即发起内容共享，默认为0 0-否； 1-是；
	 **/
	private Integer doubleflow = 0;
	/**
	 * （必填）是否开启语音激励 0-否； 1-是；
	 **/
	private Integer voice_activity_detection;
	/** 语音激励敏感度(s),支持5、15、30、60 **/
	private Integer vacinterval;
	/**
	 * （必填） 级联模式 0-简单级联； 1-合并级联；
	 **/
	private Integer cascade_mode;
	/**
	 * （必填）是否级联上传 0-否； 1-是；
	 **/
	private Integer cascade_upload;
	/**
	 * （必填）是否级联回传 0-否； 1-是；
	 **/
	private Integer cascade_return;
	/** （必填）级联回传带宽参数 **/
	private Integer cascade_return_para;
	/**
	 * （必填）是否来宾会议室 0-否； 1-是；
	 **/
	private Integer public_conf;
	/**
	 * （必填）最大与会终端数 8-小型8方会议； 32-32方会议； 64-64方会议； 192-大型192方会议；
	 **/
	private Integer max_join_mt;
	/**
	 * （必填）会议中无终端时，是否自动结会，永久会议时默认为0 0-否； 1-是；
	 **/
	private Integer auto_end;
	/**
	 * （必填）预占资源 0-否； 1-是；
	 **/
	private Integer preoccpuy_resource;
	/**
	 * （必填）归一重整 0-不使用； 1-使用；
	 **/
	private Integer one_reforming;
	/** 创会平台moid，不填则根据用户权限取默认值 **/
	private String platform_id;
	/** 发言人 **/
	private Member speaker;
	/** 主席 **/
	private Member chairman;
	/** 混音信息 **/
	private Mix mix;
	/** （必填）主视频格式列表 **/
	// TODO VideoFormat必须为静态类，不然配置文件无法注入
	private List<VideoFormat> video_formats;
	/** 参会成员 创建虚拟会议室时可填 **/
	private List<InviteMember> invite_members;
	/**
	 * 音频格式列表 不填默认支持所有，空列表默认支持所有 1-G.722； 2-G711(ULAW)； 3-G.711(ALAW)； 4-G.729；
	 * 5-G.728； 6-G722.1.C； 7-MP3； 8-G.719； 9-MPEG-4 AAC LC； 10-MPEG-4 AAC LD；
	 * 11-MPEG-4 AAC LC(stereo)； 12-MPEG-4 AAC LD(stereo)； 13-OPUS；
	 **/
	private List<Integer> audio_formats;
	/** 追呼成员数组 **/
	private List<Member> keep_calling_members;
	/** 画面合成设置 **/
	private Vmp vmp;
	/** vip成员列表 **/
	private List<Member> vips;
	/** 轮询设置 **/
	private Poll poll;
	/**
	 * 录像设置
	 **/
	private Recorder recorder;
	/**
	 * 数据协作
	 **/
	private Dcs dcs;

	@Data
	public class Mix {
		/**
		 * 混音模式 1-智能混音； 2-定制混音；
		 **/
		private Integer mode;
		/** 制定混音时的混音成员列表 **/
		private List<Member> members;
	}

	@Data
	public class Member {
		/** 名称 最大字符长度：128个字节 **/
		private String name;
		/** 帐号 最大字符长度：128个字节 **/
		private String account;
		/**
		 * 帐号类型 1-moid； 4-非系统邮箱； 5-e164号； 6-电话； 7-ip地址； 8-别名@ip(监控前端)；
		 **/
		private Integer account_type;
	}

	@Data
	public static class VideoFormat {
		/**
		 * 主视频格式 1-MPEG; 2-H.261; 3-H.263; 4-H.264_HP; 5-H.264_BP; 6-H.265; 7-H.263+;
		 **/
		private Integer format;
		/**
		 * 主视频分辨率 1-QCIF; 2-CIF; 3-4CIF; 12-720P; 13-1080P; 14-WCIF; 15-W4CIF; 16-4k;
		 **/
		private Integer resolution;
		/** 帧率 **/
		private Integer frame;
		/** 码率 (64~8192) **/
		private Integer bitrate;
	}

	@Data
	public class InviteMember {
		/** 名称 最大字符长度：128个字节 **/
		private String name;
		/** 帐号 最大字符长度：128个字节 **/
		private String account;
		/**
		 * 帐号类型 1-moid； 4-非系统邮箱； 5-e164号； 6-电话； 7-ip地址； 8-别名@ip(监控前端)；
		 **/
		private Integer account_type;
		/** 终端呼叫码率，不可超过会议码率，默认为会议码率 **/
		private Integer bitrate;
		/**
		 * 呼叫协议 0-H323； 1-SIP；
		 **/
		private Integer protocol;
	}

	@Data
	public class Vmp {
		/**
		 * 画面合成模式 1-定制画面合成； 2-自动画面合成；
		 **/
		private Integer mode;
		/**
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
		/**
		 * 是否识别声音来源 0-否； 1-是；
		 **/
		private Integer voice_hint;
		/**
		 * 是否向终端广播 0-否； 1-是；
		 **/
		private Integer broadcast;
		/**
		 * 是否显示别名 0-否； 1-是；
		 **/
		private Integer show_mt_name;
		/** 画面合成参数 **/
		private MtMameStyle mt_name_style;
		/** 画面合成成员列表 **/
		private List<VmpMember> members;
	}

	@Data
	public class MtMameStyle {
		/**
		 * 台标字体大小，默认为：1 0-小； 1-中； 2-大；
		 **/
		private Integer font_size;
		/**
		 * 台标字体三原色#RGB格式，十六进制表示，默认为：#FFFFFF白色
		 **/
		private String font_color;
		/**
		 * 台标显示位置，默认为1 0-左上角； 1-左下角； 2-右上角； 3-右下角； 4-底部中间；
		 **/
		private Integer position;
	}

	@Data
	public class VmpMember {
		/**
		 * 名称 最大字符长度：128个字节 仅当跟随类型为会控指定时才需要输入
		 **/
		private String name;
		/**
		 * 帐号 最大字符长度：128个字节 仅当跟随类型为会控指定时才需要输入
		 **/
		private String account;
		/**
		 * 帐号类型 1-moid； 4-非系统邮箱； 5-e164号； 6-电话； 7-ip地址； 8-别名@ip(监控前端)；
		 **/
		private Integer account_type;
		/**
		 * 跟随类型 1-会控指定； 2-发言人跟随； 3-管理方跟随； 4-会议轮询跟随； 6-单通道轮询； 7-内容共享跟随；
		 **/
		private Integer member_type;
		/**
		 * 在画画合成中的位置
		 **/
		private Integer chn_idx;
		/** 单通道轮询设置 **/
		private VmpMemberPoll poll;

	}

	@Data
	public class VmpMemberPoll {
		/**
		 * 轮询模式 1-视频轮询； 3-音视频轮询；
		 **/
		private Integer mode;
		/**
		 * 轮询次数，0无限次轮询
		 **/
		private Integer num;
		/**
		 * 轮询间隔时间(秒)
		 **/
		private Integer keep_time;
		/**
		 * 轮询成员列表
		 **/
		private List<Member> members;
	}

	@Data
	public class Poll {
		/**
		 * 轮询模式 1-视频轮询； 3-音视频轮询；
		 **/
		private Integer mode;
		/**
		 * 轮询次数，0无限次轮询
		 **/
		private Integer num;
		/**
		 * 轮询间隔时间(秒)
		 **/
		private Integer keep_time;
		/**
		 * 轮询成员列表
		 **/
		private List<Member> members;
	}

	@Data
	public class Recorder {
		/**
		 * 发布模式 0-不发布； 1-发布；
		 **/
		private Integer publish_mode;
		/**
		 * 是否内容共享录像 0-否； 1-是；
		 **/
		private Integer dual_stream;
		/**
		 * 是否支持免登陆观看直播 0-不支持； 1-支持；
		 **/
		private Integer anonymous;
		/**
		 * 录像模式 1-录像； 2-直播； 3-录像+直播；
		 **/
		private Integer recorder_mode;
		/** VRS的moid **/
		private String vrs_id;
	}

	@Data
	public class Dcs {
		/**
		 * 数据协作模式 0-关闭数据协作； 1-管理方控制； 2-自由协作；
		 **/
		private Integer mode;
	}

}
