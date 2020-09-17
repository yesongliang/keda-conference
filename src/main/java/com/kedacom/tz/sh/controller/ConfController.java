package com.kedacom.tz.sh.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.alibaba.fastjson.JSONObject;
import com.kedacom.tz.sh.constant.ConferenceConstant;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.controller.request.AddMembersParam;
import com.kedacom.tz.sh.controller.request.ConfMtCameraParam;
import com.kedacom.tz.sh.controller.request.ConfMtVolumeParam;
import com.kedacom.tz.sh.controller.request.ConfSMSParam;
import com.kedacom.tz.sh.controller.request.ConferenceMemberParam;
import com.kedacom.tz.sh.controller.request.CreateConferenceParam;
import com.kedacom.tz.sh.controller.request.DelayConfTimeParam;
import com.kedacom.tz.sh.controller.request.MixOperateParam;
import com.kedacom.tz.sh.controller.request.MonitorOperateParam;
import com.kedacom.tz.sh.controller.request.MonitorParam;
import com.kedacom.tz.sh.controller.request.OperateConfMtParam;
import com.kedacom.tz.sh.controller.request.OperateConfParam;
import com.kedacom.tz.sh.controller.request.OperateMembersParam;
import com.kedacom.tz.sh.controller.request.VmpOperateParam;
import com.kedacom.tz.sh.controller.request.VmpOperateParam.Member;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.model.AddMtsModel;
import com.kedacom.tz.sh.model.AddMtsModel.MtModel;
import com.kedacom.tz.sh.model.ConfSMSModel;
import com.kedacom.tz.sh.model.ConferenceInfoModel;
import com.kedacom.tz.sh.model.CreateConferenceModel;
import com.kedacom.tz.sh.model.CreateConferenceModel.InviteMember;
import com.kedacom.tz.sh.model.CreateConferenceModel.VideoFormat;
import com.kedacom.tz.sh.model.MixOperateModel;
import com.kedacom.tz.sh.model.MonitoNeediframeModel;
import com.kedacom.tz.sh.model.MonitorHeartbeatModel;
import com.kedacom.tz.sh.model.MonitorOperateModel;
import com.kedacom.tz.sh.model.MtInfoModel;
import com.kedacom.tz.sh.model.OperateMtsModel;
import com.kedacom.tz.sh.model.VmpOperateModel;
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

	@PostMapping("conf/member/operate")
	@ApiOperation(value = "操作会议成员：type=1-批量删除终端;type=2-批量呼叫终端;type=3-批量挂断终端")
	public void operateMembers(@RequestBody @Validated OperateMembersParam param) {
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
		// TODO 可改造为从缓存获取
		String url = String.format(ConferenceURL.GET_CONF_INFO.getUrl(), platform.getIp(), platform.getPort(), confId, platform.getToken());
		ConferenceInfoModel confInfo = conferenceService.getConfInfo(url, platform.getCookie());
		return confInfo;
	}

	@GetMapping("conf/mt/info/get")
	@ApiOperation(value = "获取与会终端信息")
	public MtInfoModel getMtInfo(Long key, String confId, String mtId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// TODO 可改造为从缓存获取
		String url = String.format(ConferenceURL.GET_CONF_MT_INFO.getUrl(), platform.getIp(), platform.getPort(), confId, mtId, platform.getToken());
		MtInfoModel mtInfo = conferenceService.getMtInfo(url, platform.getCookie());
		return mtInfo;
	}

	@GetMapping("conf/list/get")
	@ApiOperation(value = "获取视频会议列表")
	public List<ConferenceInfoModel> getConfList(Long key) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// TODO 可改造为从缓存获取
		String url = String.format(ConferenceURL.GET_CONF_LIST.getUrl(), platform.getIp(), platform.getPort(), platform.getToken());
		List<ConferenceInfoModel> confList = conferenceService.getConfList(url, platform.getCookie());
		return confList;
	}

	@GetMapping("conf/mt/list/get")
	@ApiOperation(value = "获取本级会议终端列表")
	public List<MtInfoModel> getMtList(Long key, String confId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// TODO 可改造为从缓存获取
		String mt_list_url = String.format(ConferenceURL.GET_CONF_MT_LIST.getUrl(), platform.getIp(), platform.getPort(), confId, platform.getToken());
		List<MtInfoModel> mtList = conferenceService.getMtList(mt_list_url, platform.getCookie());
		return mtList;
	}

	@GetMapping("conf/chairman/get")
	@ApiOperation(value = "获取会议主席")
	public String getChairman(Long key, String confId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// TODO 可改造为从缓存获取
		String url = String.format(ConferenceURL.GET_CHAIRMAN.getUrl(), platform.getIp(), platform.getPort(), confId, platform.getToken());
		String chairman = conferenceService.getChairman(url, platform.getCookie());
		return chairman;
	}

	// 取消主席mtId填空
	@PostMapping("conf/chairman/put")
	@ApiOperation(value = "指定会议主席")
	public void putChairman(Long key, String confId, String mtId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		String url = String.format(ConferenceURL.PUT_CHAIRMAN.getUrl(), platform.getIp(), platform.getPort(), confId);
		JSONObject jsonObject = new JSONObject();
		if (StringUtils.isEmpty(mtId)) {
			jsonObject.put(ConferenceConstant.MT_ID, "");
		} else {
			jsonObject.put(ConferenceConstant.MT_ID, mtId);
		}
		conferenceService.putChairman(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
	}

	@GetMapping("conf/speaker/get")
	@ApiOperation(value = "获取会议发言人")
	public String getSpeaker(Long key, String confId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// TODO 可改造为从缓存获取
		String url = String.format(ConferenceURL.GET_SPEAKER.getUrl(), platform.getIp(), platform.getPort(), confId, platform.getToken());
		String speaker = conferenceService.getSpeaker(url, platform.getCookie());
		return speaker;
	}

	// 取消发言人mtId填空
	@PostMapping("conf/speaker/put")
	@ApiOperation(value = "指定会议发言人")
	public void putSpeaker(Long key, String confId, String mtId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		String url = String.format(ConferenceURL.PUT_SPEAKER.getUrl(), platform.getIp(), platform.getPort(), confId);
		JSONObject jsonObject = new JSONObject();
		if (StringUtils.isEmpty(mtId)) {
			jsonObject.put(ConferenceConstant.MT_ID, "");
		} else {
			jsonObject.put(ConferenceConstant.MT_ID, mtId);
		}
		conferenceService.putSpeaker(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
	}

	@PostMapping("conf/mt/operate")
	@ApiOperation(value = "type:1-指定会议双流源;2-取消会议双流源;3-终端静音;4-取消终端静音;5-终端哑音;6-取消终端哑音;")
	public void operateConfMt(@RequestBody @Validated OperateConfMtParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		JSONObject jsonObject = new JSONObject();
		// type=1-指定会议双流源
		if (param.getType() == 1) {
			jsonObject.put(ConferenceConstant.MT_ID, param.getMtId());
			String url = String.format(ConferenceURL.PUT_DUALSTREAM.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.putDualstream(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=2-取消会议双流源
		} else if (param.getType() == 2) {
			jsonObject.put(ConferenceConstant.MT_ID, param.getMtId());
			String url = String.format(ConferenceURL.DELETE_DUALSTREAM.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.deleteDualstream(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=3-终端静音
		} else if (param.getType() == 3) {
			jsonObject.put(ConferenceConstant.VALUE, 1);
			String url = String.format(ConferenceURL.CONF_MT_SILENCE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getMtId());
			conferenceService.confMTSilence(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=4-取消终端静音
		} else if (param.getType() == 4) {
			jsonObject.put(ConferenceConstant.VALUE, 0);
			String url = String.format(ConferenceURL.CONF_MT_SILENCE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getMtId());
			conferenceService.confMtMute(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=5-终端哑音
		} else if (param.getType() == 5) {
			jsonObject.put(ConferenceConstant.VALUE, 1);
			String url = String.format(ConferenceURL.CONF_MT_MUTE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getMtId());
			conferenceService.confMTSilence(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=6-取消终端哑音
		} else if (param.getType() == 6) {
			jsonObject.put(ConferenceConstant.VALUE, 0);
			String url = String.format(ConferenceURL.CONF_MT_MUTE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getMtId());
			conferenceService.confMtMute(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
		}
	}

	@GetMapping("conf/dualstream/get")
	@ApiOperation(value = "获取会议双流源")
	public String getDualstream(Long key, String confId) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// TODO 可改造为从缓存获取
		String url = String.format(ConferenceURL.GET_DUALSTREAM.getUrl(), platform.getIp(), platform.getPort(), confId, platform.getToken());
		String dualstream = conferenceService.getDualstream(url, platform.getCookie());
		return dualstream;
	}

	@PostMapping("conf/volume/operate")
	@ApiOperation(value = "type:1-会场静音;2-取消会场静音;3-会场哑音;4-取消会场哑音;")
	public void operateConfVolume(@RequestBody @Validated OperateConfParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		JSONObject jsonObject = new JSONObject();
		// type=1-会场静音
		if (param.getType() == 1) {
			jsonObject.put(ConferenceConstant.VALUE, 1);
			String url = String.format(ConferenceURL.CONF_SILENCE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.confSilence(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=2-取消会场静音
		} else if (param.getType() == 2) {
			jsonObject.put(ConferenceConstant.VALUE, 0);
			String url = String.format(ConferenceURL.CONF_SILENCE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.confSilence(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=3-会场哑音
		} else if (param.getType() == 3) {
			jsonObject.put(ConferenceConstant.VALUE, 1);
			String url = String.format(ConferenceURL.CONF_MUTE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.confMute(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
			// type=4-取消会场哑音
		} else if (param.getType() == 4) {
			jsonObject.put(ConferenceConstant.VALUE, 0);
			String url = String.format(ConferenceURL.CONF_MUTE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.confMute(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
		}
	}

	@PostMapping("conf/end/delay")
	@ApiOperation(value = "延长会议时间")
	public void DelayConfTime(@RequestBody @Validated DelayConfTimeParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ConferenceConstant.DELAY_TIME, param.getDelayTime());
		String url = String.format(ConferenceURL.DELAY_CONF.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
		conferenceService.delayConfTime(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
	}

	@PostMapping("conf/message/send")
	@ApiOperation(value = "发送短消息")
	public void sendMessage(@RequestBody @Validated ConfSMSParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}

		if (!StringUtils.isEmpty(param.getMessage()) && param.getMessage().length() > 750) {
			throw new BusinessException("消息内容长度超出限制");
		}

		ConfSMSModel confSMSModel = new ConfSMSModel();
		for (String mtId : param.getMtIds()) {
			if (StringUtils.isEmpty(mtId) || mtId.length() > 24) {
				throw new BusinessException("会议成员唯一标识不符合规则");
			}
			confSMSModel.addMt(mtId);
		}
		confSMSModel.setRoll_num(param.getRollNum());
		confSMSModel.setRoll_speed(param.getRollSpeed());
		confSMSModel.setType(param.getType());
		confSMSModel.setMessage(param.getMessage());
		String url = String.format(ConferenceURL.CONF_SMS.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
		conferenceService.sendMessage(url, platform.getToken(), JSON.toJSONString(confSMSModel), platform.getCookie());
	}

	@PostMapping("conf/mix/operate")
	@ApiOperation(value = "会议混音相关操作:type=1-开始会议混音；type=2-结束会议混音；type=3-添加会议混音成员；type=4-删除会议混音成员")
	public void operateConfMix(@RequestBody @Validated MixOperateParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}

		String url = null;
		switch (param.getType()) {

		case 1:// type=1-开始会议混音
			if (param.getMode() == null || (param.getMode() != 1 && param.getMode() != 2)) {
				throw new BusinessException("不支持的混音模式");
			}
			MixOperateModel start = new MixOperateModel();
			start.setMode(param.getMode());
			// 2-定制混音；
			if (param.getMode() == 2) {
				if (CollectionUtils.isEmpty(param.getMtIds())) {
					throw new BusinessException("定制混音时混音列表不能为空");
				} else {
					for (String mtId : param.getMtIds()) {
						if (StringUtils.isEmpty(mtId) || mtId.length() > 24) {
							throw new BusinessException("会议成员唯一标识不符合规则");
						}
						start.addMember(mtId);
					}
				}
			}
			url = String.format(ConferenceURL.START_CONF_MIX.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.startMix(url, platform.getToken(), JSON.toJSONString(start), platform.getCookie());
			break;
		case 2:// type=2-结束会议混音
			url = String.format(ConferenceURL.STOP_CONF_MIX.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.stopMix(url, platform.getToken(), platform.getCookie());
			break;
		case 3:// type=3-添加会议混音成员
			MixOperateModel add = new MixOperateModel();
			if (CollectionUtils.isEmpty(param.getMtIds())) {
				throw new BusinessException("mtIds不能为空");
			} else {
				for (String mtId : param.getMtIds()) {
					if (StringUtils.isEmpty(mtId) || mtId.length() > 24) {
						throw new BusinessException("会议成员唯一标识不符合规则");
					}
					add.addMember(mtId);
				}
			}
			url = String.format(ConferenceURL.CONF_MIX_MEMBER_ADD.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.addMixMember(url, platform.getToken(), JSON.toJSONString(add), platform.getCookie());
			break;
		case 4:// type=4-删除会议混音成员
			MixOperateModel delete = new MixOperateModel();
			if (CollectionUtils.isEmpty(param.getMtIds())) {
				throw new BusinessException("mtIds不能为空");
			} else {
				for (String mtId : param.getMtIds()) {
					if (StringUtils.isEmpty(mtId) || mtId.length() > 24) {
						throw new BusinessException("会议成员唯一标识不符合规则");
					}
					delete.addMember(mtId);
				}
			}
			url = String.format(ConferenceURL.CONF_MIX_MEMBER_DELETE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.deleteMixMember(url, platform.getToken(), JSON.toJSONString(delete), platform.getCookie());
			break;
		default:
			break;
		}
	}

	@PostMapping("conf/vmp/operate")
	@ApiOperation(value = "会议画面合成相关操作:type=1-开始会议画面合成；type=2-修改会议画面合成；type=3-结束会议画面合成")
	public void operateConfVmp(@RequestBody @Validated VmpOperateParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		String url = null;
		// type=3-结束会议画面合成
		if (param.getType() == 3) {
			url = String.format(ConferenceURL.STOP_CONF_VMP.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.stopVmp(url, platform.getToken(), platform.getCookie());
		} else {
			VmpOperateModel vmpOperateModel = new VmpOperateModel();
			// TODO 目前暂提高两种模式支持
			if (param.getMode() == null || (param.getMode() != 1 && param.getMode() != 2)) {
				throw new BusinessException("不支持的画面合成模式");
			}
			vmpOperateModel.setMode(param.getMode());

			// 会议平台在某些情况下不需要但又必须填的数据，设置默认值
			vmpOperateModel.setLayout(1);
			vmpOperateModel.setMembers(new ArrayList<>());

			// 1-定制画面合成
			if (param.getMode() == 1) {
				List<Integer> layouts = Arrays.asList(1, 2, 3, 61, 62, 63, 23, 45, 35, 6, 13, 7, 8, 18, 38, 39, 19, 17, 20, 11, 46, 48, 51, 14, 54, 56, 59, 27);
				if (param.getLayout() == null || !layouts.contains(param.getLayout().intValue())) {
					throw new BusinessException("不支持的画面合成风格");
				}
				vmpOperateModel.setLayout(param.getLayout());
				if (!CollectionUtils.isEmpty(param.getMembers())) {
					for (Member member : param.getMembers()) {
						Integer chnIdx = member.getChnIdx();
						String mtId = member.getMtId();
						if (chnIdx == null || chnIdx < 0) {
							throw new BusinessException("画面合成通道索引不符合规则");
						}
						if (StringUtils.isEmpty(mtId) || mtId.length() > 24) {
							throw new BusinessException("通道终端号不符合规则");
						}
						vmpOperateModel.addMember(chnIdx, mtId);
					}
				}
			}
			// 1-开始会议画面合成
			if (param.getType() == 1) {
				url = String.format(ConferenceURL.START_CONF_VMP.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
				conferenceService.startVmp(url, platform.getToken(), JSON.toJSONString(vmpOperateModel), platform.getCookie());
				// 2-修改会议画面合成
			} else {
				url = String.format(ConferenceURL.MODIFY_CONF_VMP.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
				conferenceService.modifyVmp(url, platform.getToken(), JSON.toJSONString(vmpOperateModel), platform.getCookie());
			}
		}
	}

	@PostMapping("conf/monitor/mt")
	@ApiOperation(value = "监控操作:mode=0-会议终端视频监控；mode=1-会议终端音频监控；")
	public void confMtMonitor(@RequestBody @Validated MonitorParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 参数构建
		MonitorOperateModel model = new MonitorOperateModel();
		model.setMode(param.getMode());
		model.setSrc(param.getMtId());
		model.setDst(param.getIp(), param.getPort());
		// 构建URL
		String url = String.format(ConferenceURL.MONITORS_OPERATE.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
		conferenceService.monitor(url, platform.getToken(), JSON.toJSONString(model), platform.getCookie());
	}

	@PostMapping("conf/monitor/operate")
	@ApiOperation(value = "监控相关操作:1-取消监控；2-监控心跳保活；3-监控请求关键帧")
	public void operateMonitor(@RequestBody @Validated MonitorOperateParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		String url = null;
		switch (param.getType()) {
		case 1:// 1-取消监控
			url = String.format(ConferenceURL.MONITORS_CANCEL.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getIp(), param.getPort());
			conferenceService.cancelMonitor(url, platform.getToken(), platform.getCookie());
			break;
		case 2:// 2-监控心跳保活
			MonitorHeartbeatModel monitorHeartbeatModel = new MonitorHeartbeatModel();
			monitorHeartbeatModel.addMember(param.getIp(), param.getPort());
			url = String.format(ConferenceURL.MONITORS_HEARTBEAT.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.heartbeatMonitor(url, platform.getToken(), JSON.toJSONString(monitorHeartbeatModel), platform.getCookie());
			break;
		case 3:// 3-监控请求关键帧
			MonitoNeediframeModel monitoNeediframeModel = new MonitoNeediframeModel();
			monitoNeediframeModel.setDst(param.getIp(), param.getPort());
			url = String.format(ConferenceURL.MONITORS_NEEDIFRAME.getUrl(), platform.getIp(), platform.getPort(), param.getConfId());
			conferenceService.neediframeMonitor(url, platform.getToken(), JSON.toJSONString(monitoNeediframeModel), platform.getCookie());
			break;
		default:
			break;
		}
	}

	@PostMapping("conf/mt/volume/operate")
	@ApiOperation(value = "修改终端音量操作")
	public void confMtVolumeOperate(@RequestBody @Validated ConfMtVolumeParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ConferenceConstant.VOL_MODE, param.getMode());
		jsonObject.put(ConferenceConstant.VOL_VALUE, param.getValue());
		String url = String.format(ConferenceURL.CONF_MT_VOLUME.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getMtId());
		conferenceService.volumeControl(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
	}

	@PostMapping("conf/mt/camera/operate")
	@ApiOperation(value = "终端摄像头控制操作")
	public void confMtCameraOperate(@RequestBody @Validated ConfMtCameraParam param) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(param.getPlatformId());
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ConferenceConstant.STATE, param.getState());
		jsonObject.put(ConferenceConstant.TYPE, param.getType());
		String url = String.format(ConferenceURL.CONF_MT_CAMERA.getUrl(), platform.getIp(), platform.getPort(), param.getConfId(), param.getMtId());
		conferenceService.cameraControl(url, platform.getToken(), jsonObject.toString(), platform.getCookie());
	}

}
