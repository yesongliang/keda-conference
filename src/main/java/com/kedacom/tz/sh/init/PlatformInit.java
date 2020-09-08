package com.kedacom.tz.sh.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.kedacom.tz.sh.thread.ConferencePlatform;
import com.kedacom.tz.sh.utils.ConferencePlatformManager;
import com.kedacom.tz.sh.utils.IPUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Order(value = 1)
@Slf4j
public class PlatformInit implements ApplicationRunner {

	@Autowired
	private Environment environment;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 是否使用分布式缓存
		Boolean distributed = environment.getProperty("distribute.enable", Boolean.class, false);
		// 分布式||集群
		if (!distributed) {
			log.debug("测试用，系统启动后导入两个会议平台");
			// 苏州 Version:5.2.0.4.2
			ConferencePlatform sz_platform = new ConferencePlatform("10.66.9.181", "KzlAyn4GvvgP", "wNctWrafAwtm", "administrator", "888888");
			ConferencePlatformManager.addPlatform(IPUtils.ipToLong(sz_platform.getIp()), sz_platform);
			// 上海 Version:V6.0.0.4.0
			ConferencePlatform sh_platform = new ConferencePlatform("172.16.188.209", "KzlAyn4GvvgP", "wNctWrafAwtm", "administrator", "888888");
			ConferencePlatformManager.addPlatform(IPUtils.ipToLong(sh_platform.getIp()), sh_platform);
		}
	}

}
