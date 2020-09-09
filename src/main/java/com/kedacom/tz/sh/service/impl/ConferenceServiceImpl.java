package com.kedacom.tz.sh.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.tz.sh.constant.ConferenceConstant;
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
		ResponseEntity<String> post = httpUtils.post(url, params, null);
		JSONObject jsonObject = conferenceResponseHandle(url, post);
		String token = jsonObject.getString(ConferenceConstant.ACCOUNT_TOKEN);
		return token;
	}

	@Override
	public boolean heartbeatToken(String url, List<String> cookie) {
		ResponseEntity<String> post = httpUtils.post(url, null, cookie);
		conferenceResponseHandle(url, post);
		return true;
	}

	@Override
	public List<String> login(String url, String token, String username, String password) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.USERNAME, username);
		params.put(ConferenceConstant.PASSWORD, password);
		ResponseEntity<String> post = httpUtils.post(url, params, null);
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
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
		return true;
	}

	@Override
	public String getVersion(String url, List<String> cookie) {
		ResponseEntity<String> get = httpUtils.get(url, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, get);
		String version = jsonObject.getString(ConferenceConstant.VERSION);
		return version;
	}

	@Override
	public String createConf(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, post);
		String confId = jsonObject.getString(ConferenceConstant.CONF_ID);
		return confId;
	}

	@Override
	public void endConf(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void putChairman(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_PUT);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public String getChairman(String url, String token, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		JSONObject jsonObject = conferenceResponseHandle(url, post);
		String chairmanId = jsonObject.getString(ConferenceConstant.MT_ID);
		return chairmanId;
	}

	@Override
	public void addMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void deleteMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void onlineMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public void offlineMts(String url, String token, String param, List<String> cookie) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConferenceConstant.ACCOUNT_TOKEN, token);
		params.put(ConferenceConstant.PARAMS, param);
		params.put(ConferenceConstant.METHOD, ConferenceConstant.METHOD_DELETE);
		ResponseEntity<String> post = httpUtils.post(url, params, cookie);
		conferenceResponseHandle(url, post);
	}

	@Override
	public ConferenceInfoModel getConfInfo(String url, List<String> cookie) {
		ResponseEntity<String> get = httpUtils.get(url, cookie);
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
		ResponseEntity<String> get = httpUtils.get(url, cookie);
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

}
