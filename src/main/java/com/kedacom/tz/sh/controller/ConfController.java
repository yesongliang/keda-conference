package com.kedacom.tz.sh.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.controller.request.ConferenceMemberParam;
import com.kedacom.tz.sh.controller.request.CreateConferenceParam;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.model.CreateConferenceModel;
import com.kedacom.tz.sh.model.CreateConferenceModel.InviteMember;
import com.kedacom.tz.sh.model.CreateConferenceModel.VideoFormat;
import com.kedacom.tz.sh.service.IBusinessService;
import com.kedacom.tz.sh.service.IConferenceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "会议管理及控制类接口")
public class ConfController {

	private static final Pattern CONF_NAME = Pattern.compile("[^%&*^~'\"\"?/\\<>|`\"$]+");
	private static final Pattern CONF_NAME_PREFIX = Pattern.compile("[^._@-]+");

	@Autowired
	private IConferenceService conferenceService;

	@Autowired
	private CreateConferenceModel createConferenceModel;

	@Autowired
	private IBusinessService businessService;

	@PostMapping("conf/create")
	@ApiOperation(value = "创建会议")
	public String createConf(@RequestBody @Validated CreateConferenceParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 构建URL
		String url = String.format(ConferenceURL.CREATE_CONF.getUrl(), platform.getIp(), platform.getPort());

		// 创会参数构建
		CreateConferenceModel conferenceModel = new CreateConferenceModel();
		// 从配置文件获取默认配置
		BeanUtils.copyProperties(createConferenceModel, conferenceModel);

		// 创会参数校验---start
		// 会议名称
		String name = param.getName();
		boolean flag = false;
		// 参数校验
		if (name.length() <= 32) {
			boolean matches = CONF_NAME.matcher(name).matches();
			if (matches) {
				String confName = name.substring(0, 1) + name.substring(name.length() - 1);
				boolean matches2 = CONF_NAME_PREFIX.matcher(confName).matches();
				if (matches2) {
					flag = true;
				}
			}
		}
		if (!flag) {
			throw new BusinessException("会议名称不符合规则");
		}
		conferenceModel.setName(name);

		// 会议时长
		conferenceModel.setDuration(param.getDuration());
		// 会议中无终端时，是否自动结会
		conferenceModel.setAuto_end(param.getAuto_end());
		// 主视频格式
		VideoFormat videoFormat = new VideoFormat();
		switch (param.getVideo_format()) {
		case 1:// 1-全高清(2M 1080P@60fps);
			videoFormat.setBitrate(2048);
			videoFormat.setFormat(5);
			videoFormat.setFrame(60);
			videoFormat.setResolution(13);
			break;
		case 2:// 2-高清(1M 720P@30fps);
			videoFormat.setBitrate(1024);
			videoFormat.setFormat(5);
			videoFormat.setFrame(30);
			videoFormat.setResolution(12);
			break;
		case 3:// 3-标清(256K W4CIF@25fps);
			videoFormat.setBitrate(256);
			videoFormat.setFormat(5);
			videoFormat.setFrame(25);
			videoFormat.setResolution(15);
			break;
		case 4:// 4-流畅(192K WCIF@25fps);
			videoFormat.setBitrate(192);
			videoFormat.setFormat(5);
			videoFormat.setFrame(25);
			videoFormat.setResolution(14);
			break;
		default:
			break;
		}
		List<VideoFormat> video_formats = conferenceModel.getVideo_formats();
		video_formats.clear();
		video_formats.add(videoFormat);

		conferenceModel.setBitrate(videoFormat.getBitrate());

		// 参会成员
		if (!CollectionUtils.isEmpty(param.getInvite_members())) {
			List<InviteMember> invite_members = new ArrayList<>();
			for (ConferenceMemberParam member : param.getInvite_members()) {
				InviteMember inviteMember = conferenceModel.new InviteMember();
//				inviteMember.setBitrate(conferenceModel.getBitrate());
				// 参数校验
				String account = member.getAccount();
				if (StringUtils.isEmpty(account) || account.length() > 64) {
					throw new BusinessException("账号不符合规则");
				}
				inviteMember.setAccount(account);
				inviteMember.setName(account);
				Integer account_type = member.getAccount_type();
				if (account_type != 1 && account_type != 4 && account_type != 5 && account_type != 6 && account_type != 7 && account_type != 8) {
					throw new BusinessException("不支持的账号类型");
				}
				inviteMember.setAccount_type(account_type);
				Integer protocol = member.getProtocol();
				if (protocol != 0 && protocol != 1) {
					throw new BusinessException("不支持的呼叫协议");
				}
				inviteMember.setProtocol(protocol);

				invite_members.add(inviteMember);
			}
			conferenceModel.setInvite_members(invite_members);
		}
		// 创会参数校验---end

		// 发送创会请求
		String confId = conferenceService.createConf(url, platform.getToken(), JSON.toJSONString(conferenceModel), platform.getCookie());
		return confId;
	}

	@PostMapping("conf/end")
	@ApiOperation(value = "结束会议")
	public void endConf(Long key, String confId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 构建URL
		String url = String.format(ConferenceURL.END_CONF.getUrl(), platform.getIp(), platform.getPort(), confId);
		conferenceService.endConf(url, platform.getToken(), platform.getCookie());
	}

}
