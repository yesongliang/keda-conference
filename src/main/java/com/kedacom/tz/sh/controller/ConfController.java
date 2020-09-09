package com.kedacom.tz.sh.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.controller.request.AddMembersParam;
import com.kedacom.tz.sh.controller.request.ConferenceMemberParam;
import com.kedacom.tz.sh.controller.request.CreateConferenceParam;
import com.kedacom.tz.sh.controller.request.OperateMembersParam;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.model.AddMtsModel;
import com.kedacom.tz.sh.model.AddMtsModel.MtModel;
import com.kedacom.tz.sh.model.ConferenceInfoModel;
import com.kedacom.tz.sh.model.CreateConferenceModel;
import com.kedacom.tz.sh.model.CreateConferenceModel.InviteMember;
import com.kedacom.tz.sh.model.CreateConferenceModel.VideoFormat;
import com.kedacom.tz.sh.model.MtInfoModel;
import com.kedacom.tz.sh.model.OperateMtsModel;
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

	@PostMapping("conf/mt/add")
	@ApiOperation(value = "批量添加本级终端")
	public void addMts(@RequestBody @Validated AddMembersParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 参数验证---邀请成员
		AddMtsModel addMtsModel = new AddMtsModel();
		for (ConferenceMemberParam member : param.getInvite_members()) {
			MtModel mtModel = addMtsModel.new MtModel();
			String account = member.getAccount();
			if (StringUtils.isEmpty(account) || account.length() > 64) {
				throw new BusinessException("账号不符合规则");
			}
			mtModel.setAccount(account);
			// TODO 呼叫码率，必填，后续获取视频会议信息填会议码率。（经测试可为固定值，不受会议码率影响）
			mtModel.setBitrate(1024);
			Integer account_type = member.getAccount_type();
			if (account_type != 1 && account_type != 4 && account_type != 5 && account_type != 6 && account_type != 7 && account_type != 8) {
				throw new BusinessException("不支持的账号类型");
			}
			mtModel.setAccount_type(account_type);
			Integer protocol = member.getProtocol();
			if (protocol != 0 && protocol != 1) {
				throw new BusinessException("不支持的呼叫协议");
			}
			mtModel.setProtocol(protocol);
			addMtsModel.addMt(mtModel);
		}

		// 构建URL
		String url = String.format(ConferenceURL.ADD_MTS.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
		conferenceService.addMts(url, platform.getToken(), JSON.toJSONString(addMtsModel), platform.getCookie());
	}

	@PostMapping("conf/mt/operate")
	@ApiOperation(value = "操作会议成员：type=1-批量删除终端;type=2-批量呼叫终端;type=3-批量挂断终端")
	public void operateMts(@RequestBody @Validated OperateMembersParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		OperateMtsModel operateMtsModel = new OperateMtsModel();
		for (String mtId : param.getMtIds()) {
			if (StringUtils.isEmpty(mtId) || mtId.length() > 24) {
				throw new BusinessException("会议成员唯一标识不符合规则");
			}
			operateMtsModel.addMt(mtId);
		}
		String url = null;
		switch (param.getType()) {
		case 1:// 批量删除终端
			url = String.format(ConferenceURL.DELETE_MTS.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.deleteMts(url, platform.getToken(), JSON.toJSONString(operateMtsModel), platform.getCookie());
			break;
		case 2:// 批量呼叫终端
			url = String.format(ConferenceURL.ONLINE_MTS.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.onlineMts(url, platform.getToken(), JSON.toJSONString(operateMtsModel), platform.getCookie());
			break;
		case 3:// 批量挂断终端
			url = String.format(ConferenceURL.OFFLINE_MTS.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.offlineMts(url, platform.getToken(), JSON.toJSONString(operateMtsModel), platform.getCookie());
			break;
		default:
			break;
		}
	}

	@GetMapping("conf/info/get")
	@ApiOperation(value = "获取视频会议信息")
	public ConferenceInfoModel getConfInfo(Long key, String confId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 构建URL
		String url = String.format(ConferenceURL.GET_CONF_INFO.getUrl(), platform.getIp(), platform.getPort(), confId, platform.getToken());
		ConferenceInfoModel confInfo = conferenceService.getConfInfo(url, platform.getCookie());
		return confInfo;
	}

	@GetMapping("conf/mt/info/get")
	@ApiOperation(value = "获取与会终端信息")
	public MtInfoModel getConfInfo(Long key, String confId, String mtId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 构建URL
		String url = String.format(ConferenceURL.GET_CONF_MT_INFO.getUrl(), platform.getIp(), platform.getPort(), confId, mtId, platform.getToken());
		MtInfoModel mtInfo = conferenceService.getMtInfo(url, platform.getCookie());
		return mtInfo;
	}

}
