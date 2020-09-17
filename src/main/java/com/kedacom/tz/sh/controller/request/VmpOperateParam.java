package com.kedacom.tz.sh.controller.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "会议画面合成相关操作请求参数")
public class VmpOperateParam {

	/** 会议平台唯一标识 **/
	@ApiModelProperty(value = "会议平台唯一标识")
	@NotNull(message = "不能为空")
	private Long platformId;

	/** 会议唯一标识 **/
	@ApiModelProperty(value = "会议ID")
	@NotNull(message = "不能为空")
	private String confId;

	/** 操作类型:1-开始会议画面合成；2-结束会议画面合成；3-修改会议画面合成； **/
	@ApiModelProperty(value = "1-开始会议画面合成；2-修改会议画面合成；3-结束会议画面合成；")
	@Min(value = 1, message = "不支持的操作类型")
	@Max(value = 3, message = "不支持的操作类型")
	private Integer type;

	/** 画面合成模式 1-定制画面合成； 2-自动画面合成；开启/修改画面合成操作时必需 **/
	@ApiModelProperty(value = "画面合成模式 1-定制画面合成； 2-自动画面合成；开启/修改画面合成操作时必需")
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
	@ApiModelProperty(value = "画面合成风格；开启/修改画面合成操作时必需")
	private Integer layout;
	@ApiModelProperty(value = "画面合成成员数组；开启/修改画面合成操作时必需")
	private List<Member> members;

	@Data
	@ApiModel(description = "画面合成成员")
	public class Member {
		/** 画面合成通道索引，从0开始 **/
		@ApiModelProperty(value = "画面合成通道索引，从0开始")
		private Integer chnIdx;
		/** 通道终端号，仅当通道中为会控指定时需要 最大字符长度：48个字节 **/
		@ApiModelProperty(value = "通道终端号")
		private String mtId;
	}
}
