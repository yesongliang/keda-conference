package com.kedacom.tz.sh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.controller.response.PlatformVo;
import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.service.IBusinessService;
import com.kedacom.tz.sh.service.IConferenceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "会议平台信息类接口")
public class PlatformInfoController {

	@Autowired
	private IConferenceService conferenceService;

	@Autowired
	private IBusinessService businessService;

	@GetMapping("get/platform/version/{key}")
	@ApiOperation(value = "获取会议平台的版本,会议平台6.0后开始支持")
	public String getPlatformVersion(@PathVariable("key") Long key) {
		// 获取会议平台
		PlatformVo platform = businessService.getPlatformByKey(key);
		if (platform == null || !platform.isLogin() || !platform.isConnected()) {
			throw new BusinessException("会议平台暂不可用");
		}
		// 构建URL
		String url = String.format(ConferenceURL.VERSION.getUrl(), platform.getIp(), platform.getPort());
		String version = conferenceService.getVersion(url, platform.getCookie());
		return version;
	}
}
