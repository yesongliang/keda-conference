package com.kedacom.tz.sh.cache;

/**
 * 业务redis缓存常量定义
 * 
 * @author ysl
 *
 */
public interface RedisCacheConstant {

	/** 平台基础数据缓存前缀 **/
	String PLATFORM_BASE_DATA_PRE = "platform_base_data:";

	/** 平台扩展数据缓存前缀 **/
	String PLATFORM_EXTEND_DATA_PRE = "platform_extend_data:";

	/** 通配符 **/
	String PATTERN_ALL = "*";

	/** 会议信息缓存前缀 conf:platformId:confId **/
	String CONF_INFO_PRE = "conf:";

	/** 会议终端信息缓存前缀 mt:platformId:confId:mtId **/
	String MT_INFO_PRE = "mt:";

	/** 会议画面合成信息缓存前缀 vmp:platformId:confId **/
	String VMP_INFO_PRE = "vmp:";

	/** 会议混音信息缓存前缀 mix:platformId:confId **/
	String MIX_INFO_PRE = "mix:";
}
