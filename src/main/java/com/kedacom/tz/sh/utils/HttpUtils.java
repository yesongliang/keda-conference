package com.kedacom.tz.sh.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

/**
 * 会议平台http请求工具类---RestTemplate实现
 * 
 * @author songliang.ye
 *
 */
@Component
@Slf4j
public class HttpUtils {

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 注意：get请求,请求参数需放在url上，否则报错：method GET must not have a request body.
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @param cookie
	 * @return
	 */
	@HystrixCommand(fallbackMethod = "requestFailed")
	public ResponseEntity<String> request(String url, HttpMethod method, Map<String, String> params, List<String> cookie) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		if (!CollectionUtils.isEmpty(cookie)) {
			headers.put(HttpHeaders.COOKIE, cookie);
		}
		MultiValueMap<String, String> map = null;
		if (params != null && params.size() > 0) {
			map = new LinkedMultiValueMap<String, String>();
			for (Entry<String, String> entry : params.entrySet()) {
				map.add(entry.getKey(), entry.getValue());
			}
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> exchange = restTemplate.exchange(url, method, request, String.class);
		return exchange;
	}

	/**
	 * 熔断机制,服务降级
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @param cookies
	 * @param throwable
	 * @return
	 */
	public ResponseEntity<String> requestFailed(String url, HttpMethod method, Map<String, String> params, List<String> cookies, Throwable throwable) {
		throwable.printStackTrace();
		log.error("http request failed;url={},method={},params={},cookie={}", url, method, params, cookies);
		return null;
	}

//	/**
//	 * post请求，默认键值对参数
//	 * 
//	 * @param url
//	 * @param params
//	 * @return
//	 */
//	@HystrixCommand(fallbackMethod = "postFailed")
//	public ResponseEntity<String> post(String url, Map<String, String> params, List<String> cookie) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		if (!CollectionUtils.isEmpty(cookie)) {
//			headers.put(HttpHeaders.COOKIE, cookie);
//		}
//		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//		if (params != null && params.size() > 0) {
//			for (Entry<String, String> entry : params.entrySet()) {
//				map.add(entry.getKey(), entry.getValue());
//			}
//		}
//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
//		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//		return exchange;
//	}
//
//	public ResponseEntity<String> postFailed(String url, Map<String, String> params, List<String> cookies, Throwable throwable) {
//		throwable.printStackTrace();
//		log.error("熔断机制启动，http post request failed;url={}", url);
//		return null;
//	}
//
//	/**
//	 * get请求,可携带cookie（请求参数在url）
//	 * 
//	 * @param url
//	 * @param cookie
//	 * @return
//	 */
//	@HystrixCommand(fallbackMethod = "getFailed")
//	public ResponseEntity<String> get(String url, List<String> cookie) {
//		HttpHeaders headers = new HttpHeaders();
//		if (!CollectionUtils.isEmpty(cookie)) {
//			headers.put(HttpHeaders.COOKIE, cookie);
//		}
//		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);
//		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
//		return exchange;
//	}
//
//	public ResponseEntity<String> getFailed(String url, List<String> cookies, Throwable throwable) {
//		throwable.printStackTrace();
//		log.error("熔断机制启动，http get request failed;url={}", url);
//		return null;
//	}

}
