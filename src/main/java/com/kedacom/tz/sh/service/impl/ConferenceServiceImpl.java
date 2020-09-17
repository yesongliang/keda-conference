package com.kedacom.tz.sh.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.tz.sh.constant.ConferenceConstant;
import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.model.ConferenceInfoModel;
import com.kedacom.tz.sh.model.MtInfoModel;
import com.kedacom.tz.sh.service.IConferenceService;
import com.kedacom.tz.sh.utils.HttpUtils;
import com.kedacom.tz.sh.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConferenceServiceImpl implements IConferenceService {

	@Autowired
	private HttpUtils httpUtils;

	/**
	 * 会议请求响应统一处理
	 * 
	 * @param url      请求url
	 * @param response 请求响应
	 * 
	 * @return
	 */
	private JSONObject conferenceResponseHandle(String url, ResponseEntity<String> response) {
		log.debug("http response:{}", response);
		JSONObject jsonObject = null;
		// 熔断
		if (response == null) {
			throw new BusinessException("请求会议平台服务失败：熔断机制触发,网络不通或会议平台服务不可用");
		}
		// 状态码
		int statusCodeValue = response.getStatusCodeValue();
		if (statusCodeValue != 200) {
			log.error("请求url={},HTTP status code={},请求失败!", url, statusCodeValue);
			throw new BusinessException("请求会议平台服务失败：http响应状态码=" + statusCodeValue);
		}
		// 响应
		String body = response.getBody();
		try {
			jsonObject = JSON.parseObject(body);
		} catch (Exception e) {
			log.error("请求url={},response={},解析响应失败:数据格式不是json!", url, body);
			throw new BusinessException("请求会议平台服务失败：响应数据格式不是json");
		}
		if (jsonObject != null) {
			int code = jsonObject.getIntValue(ConferenceConstant.SUCCESS);
			// 失败
			if (code == 0) {
				int error_code = jsonObject.getIntValue(ConferenceConstant.ERROR_CODE);
				log.error("请求url={},error_code={},请求失败,请参照错误码表分析!", url, error_code);
				throw new BusinessException("请求会议平台服务失败：请参照会议平台错误码表分析,error_code=" + error_code);
			}
		}
		return jsonObject;
	}

	@Override
	public String getToken(String url, String oauth_consumer_key, String oauth_consumer_secret) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.OAUTH_CONSUMER_KEY, oauth_consumer_key);
		params.put(ConferenceConstant.OAUTH_CONSUMER_SECRET, oauth_consumer_secret);
//		ResponseEntity<String> post = httpUtils.post(url, params, null);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.TOKEN.getHttpMethod(), params, null);
		JSONObject jsonObject = conferenceResponseHandle(url, post);
		String token = jsonObject.getString(ConferenceConstant.ACCOUNT_TOKEN);
		return token;
	}

	@Override
	public boolean heartbeatToken(String url, List<String> cookie) {
//		ResponseEntity<String> post = httpUtils.post(url, null, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.HEARTBEAT_TOKEN.getHttpMethod(), null, null);
		conferenceResponseHandle(url, post);
		return true;
	}

	@Override
	public List<String> login(String url, String token, String username, String password) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.USERNAME, username);
		params.put(ConferenceConstant.PASSWORD, password);
//		ResponseEntity<String> post = httpUtils.post(url, params, null);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.LOGIN.getHttpMethod(), params, null);
		conferenceResponseHandle(url, post);
		// 解析cookie
		HttpHeaders headers = post.getHeaders();
		List<String> set_CookieList = headers.get(ConferenceConstant.COOKIE);
		return set_CookieList;
	}

	@Override
	public boolean heartbeatUser(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.HEARTBEAT_USER.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
		return true;
	}

	@Override
	public String getVersion(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.VERSION.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		String version = jsonObject.getString(ConferenceConstant.VERSION);
		return version;
	}

	@Override
	public String createConf(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CREATE_CONF.getHttpMethod(), params, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, post);
		String confId = jsonObject.getString(ConferenceConstant.CONF_ID);
		return confId;
	}

	@Override
	public void endConf(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.END_CONF.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void putChairman(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_PUT);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.PUT_CHAIRMAN.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public String getChairman(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_CHAIRMAN.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		String chairman = jsonObject.getString(ConferenceConstant.MT_ID);
		return chairman;
	}

	@Override
	public void putSpeaker(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_PUT);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.PUT_SPEAKER.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public String getSpeaker(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_SPEAKER.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		String speaker = jsonObject.getString(ConferenceConstant.MT_ID);
		return speaker;
	}

	@Override
	public void addMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.ADD_MTS.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void deleteMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.DELETE_MTS.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void onlineMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.ONLINE_MTS.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void offlineMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.OFFLINE_MTS.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public ConferenceInfoModel getConfInfo(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_CONF_INFO.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		try {
			// 返回值构建
			ConferenceInfoModel conferenceInfo = JsonUtil.analysis(ConferenceInfoModel.class, jsonObject);
			return conferenceInfo;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new BusinessException("Json数据解析异常");
		}
	}

	@Override
	public MtInfoModel getMtInfo(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_CONF_MT_INFO.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		try {
			// 返回值构建
			MtInfoModel mtInfo = JsonUtil.analysis(MtInfoModel.class, jsonObject);
			return mtInfo;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new BusinessException("Json数据解析异常");
		}
	}

	@Override
	public List<ConferenceInfoModel> getConfList(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_CONF_LIST.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		try {
			List<ConferenceInfoModel> list = new ArrayList<>();
			Integer integer = jsonObject.getInteger("total");
			if (integer > 0) {
				JSONArray jsonArray = jsonObject.getJSONArray("confs");
				for (int i = 0; i < jsonArray.size(); i++) {
					ConferenceInfoModel conferenceInfoModel = JsonUtil.analysis(ConferenceInfoModel.class, jsonArray.getJSONObject(i));
					list.add(conferenceInfoModel);
				}
			}
			return list;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new BusinessException("Json数据解析异常");
		}
	}

	@Override
	public List<MtInfoModel> getMtList(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_CONF_MT_LIST.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		try {
			List<MtInfoModel> list = new ArrayList<>();
			JSONArray jsonArray = jsonObject.getJSONArray("mts");
			for (int i = 0; i < jsonArray.size(); i++) {
				MtInfoModel mtInfoModel = JsonUtil.analysis(MtInfoModel.class, jsonArray.getJSONObject(i));
				list.add(mtInfoModel);
			}
			return list;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new BusinessException("Json数据解析异常");
		}
	}

	@Override
	public void putDualstream(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_PUT);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.PUT_DUALSTREAM.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void deleteDualstream(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
//		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
//		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.DELETE_DUALSTREAM.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public String getDualstream(String url, List<String> cookie) {
//		ResponseEntity<String> get = httpUtils.get(url, cookie);
		ResponseEntity<String> get = httpUtils.request(url, ConferenceURL.GET_DUALSTREAM.getHttpMethod(), null, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		String dualstream = jsonObject.getString(ConferenceConstant.MT_ID);
		return dualstream;
	}

	@Override
	public void delayConfTime(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.DELAY_CONF.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void confSilence(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_SILENCE.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void confMute(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MUTE.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void confMTSilence(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MT_SILENCE.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void confMtMute(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MT_MUTE.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void sendMessage(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_SMS.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void startMix(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.START_CONF_MIX.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void stopMix(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.STOP_CONF_MIX.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void addMixMember(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MIX_MEMBER_ADD.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void deleteMixMember(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MIX_MEMBER_DELETE.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void startVmp(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.START_CONF_VMP.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void modifyVmp(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.MODIFY_CONF_VMP.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void stopVmp(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.STOP_CONF_VMP.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void monitor(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.MONITORS_OPERATE.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void cancelMonitor(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.MONITORS_CANCEL.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void heartbeatMonitor(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.MONITORS_HEARTBEAT.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void neediframeMonitor(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.MONITORS_NEEDIFRAME.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void volumeControl(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MT_VOLUME.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void cameraControl(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.request(url, ConferenceURL.CONF_MT_CAMERA.getHttpMethod(), params, cookie);
		conferenceResponseHandle(url, post);
	}

}
