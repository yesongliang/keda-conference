package com.kedacom.tz.sh.cometd;

import java.util.Map;

/**
 * 会议平台推送信息处理
 * 
 * @author ysl
 *
 */
public interface IHandler {

	void handle(Map<String, String> map);

}
