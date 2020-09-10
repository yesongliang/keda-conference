package com.kedacom.tz.sh.cometd;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

import com.kedacom.tz.sh.utils.IPUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 接收订阅通道上的消息
 * 
 * @author ysl
 *
 */
@Slf4j
public class MsgListener implements ClientSessionChannel.MessageListener {

	/** 会议信息推送 /confs/{conf_id} **/
	private static final Pattern CONFINFO = Pattern.compile("^(/confs/([0-9]+))$");
	/**
	 * 终端列表(cascade_id=0,代表本级会议) /confs/{conf_id}/cascades/{cascade_id}/mts/{mt_id}
	 **/
	private static final Pattern MTLIST = Pattern.compile("^(/confs/([0-9]+)/cascades/0/mts/([0-9]+))$");

	/** 画面合成 /confs/{conf_id}/vmps/{vmp_id} **/
	private static final Pattern VMP = Pattern.compile("^(/confs/([0-9]+)/vmps/([0-9]+))$");

	/** 混音 /confs/{conf_id}/mixs/{mix_id} **/
	private static final Pattern MIX = Pattern.compile("^(/confs/([0-9]+)/mixs/([0-9]+))$");

	/** 发言人 /confs/{conf_id}/speaker **/
	private static final Pattern SPEAKER = Pattern.compile("^(/confs/([0-9]+)/speaker)$");

	/** 主席 /confs/{conf_id}/chairman **/
	private static final Pattern CHAIRMAN = Pattern.compile("^(/confs/([0-9]+)/chairman)$");

	/** 双流源 /confs/{conf_id}/dualstream **/
	private static final Pattern DUALSTREAM = Pattern.compile("^(/confs/([0-9]+)/dualstream)$");

	/** 终端选看 /confs/{conf_id}/inspections/{mt_id}/{mode} **/
	private static final Pattern INSPECTION = Pattern.compile("^(/confs/([0-9]+)/inspections/([0-9]+)/([0-9]+))$");

	/** 监控 /confs/{conf_id}/monitor/{dst_ip}/{dst_port} **/
	private static final Pattern MONITOR = Pattern.compile("^(/confs/([0-9]+)/monitor/(" + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)" + ")/([0-9]+))$");

	private String domain_id;// 5.0平台的用户域id

	private Long key; // 平台唯一标识

	public MsgListener(String domain_id, Long key) {
		this.domain_id = domain_id;
		this.key = key;
	}

	// 处理订阅消息内容
	@Override
	public void onMessage(ClientSessionChannel channel, Message message) {
		log.debug("cometD消息回调,channel={},message={}", channel, message);
		// 获取通道名
		String subChannel = message.getChannel();
		// 获取通道方法
		Map<String, Object> data = message.getDataAsMap();
		String method = data.get("method").toString();
		// 获取解析消息channel
		String realChannel = subChannel.substring(("/userdomains/" + this.domain_id).length());
		log.debug("key={},realChannel={},method={}", this.key, realChannel, method);

		// 解析消息
		Map<String, String> map = this.macthHandle(realChannel, method);

		// 未定义处理器
		if ("undefined".equals(map.get("type"))) {
			log.info("程序暂未定义处理此类消息的处理器,platformIP={},realChannel={},method={}", IPUtils.longToIP(this.key), realChannel, method);
		} else {
			MessageHandelrManager.getInstance().addMessage(key, map);
		}
	}

	/**
	 * 消息解析
	 * 
	 * @param channel
	 * @param method
	 */
	private Map<String, String> macthHandle(String channel, String method) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("key", String.valueOf(this.key));
		map.put("method", method);
		String[] splits = channel.split("/");

		/** 会议信息 **/
		// 通道 /confs/{conf_id}
		boolean is_conf_info = CONFINFO.matcher(channel).matches();
		if (is_conf_info) {
			map.put("type", "conf_info");
			map.put("conf_id", splits[2]);
			return map;
		}

		/** 终端列表 **/
		// 通道 /confs/{conf_id}/cascades/{cascade_id}/mts/{mt_id}
		// cascade_id=0,代表本级会议
		boolean is_mt_list = MTLIST.matcher(channel).matches();
		if (is_mt_list) {
			map.put("type", "mt_list");
			map.put("conf_id", splits[2]);
			map.put("mt_id", splits[6]);
			return map;
		}

		/** 画面合成 **/
		// 通道 /confs/{conf_id}/vmps/{vmp_id}
		// vmp_id:画面合成号，默认为1
		boolean is_vmp = VMP.matcher(channel).matches();
		if (is_vmp) {
			map.put("type", "vmp");
			map.put("conf_id", splits[2]);
			map.put("vmp_id", splits[4]);
			return map;
		}

		/** 混音 **/
		// 通道 /confs/{conf_id}/mixs/{mix_id}
		// mix_id:混音id,默认为1
		boolean is_mix = MIX.matcher(channel).matches();
		if (is_mix) {
			map.put("type", "mix");
			map.put("conf_id", splits[2]);
			map.put("mix_id", splits[4]);
			return map;
		}

		/** 发言人 /confs/{conf_id}/speaker **/
		boolean is_speaker = SPEAKER.matcher(channel).matches();
		if (is_speaker) {
			map.put("type", "speaker");
			map.put("conf_id", splits[2]);
			return map;
		}

		/** 主席 /confs/{conf_id}/chairman **/
		boolean is_chairman = CHAIRMAN.matcher(channel).matches();
		if (is_chairman) {
			map.put("type", "chairman");
			map.put("conf_id", splits[2]);
			return map;
		}

		/** 双流源 /confs/{conf_id}/dualstream **/
		boolean is_dualstream = DUALSTREAM.matcher(channel).matches();
		if (is_dualstream) {
			map.put("type", "dualstream");
			map.put("conf_id", splits[2]);
			return map;
		}

		/** 终端选看 /confs/{conf_id}/inspections/{mt_id}/{mode} **/
		boolean is_inspection = INSPECTION.matcher(channel).matches();
		if (is_inspection) {
			map.put("type", "inspection");
			map.put("conf_id", splits[2]);
			map.put("mt_id", splits[4]);
			map.put("mode", splits[5]);
			return map;
		}

		/** 监控 /confs/{conf_id}/monitor/{dst_ip}/{dst_port} **/
		boolean is_monitor = MONITOR.matcher(channel).matches();
		if (is_monitor) {
			map.put("type", "monitor");
			map.put("conf_id", splits[2]);
			map.put("dst_ip", splits[4]);
			map.put("dst_port", splits[5]);
			return map;
		}

		// 未定义
		map.put("type", "undefined");
		return map;
	}

}
