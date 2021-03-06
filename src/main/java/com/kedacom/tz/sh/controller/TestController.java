package com.kedacom.tz.sh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedacom.tz.sh.annotation.IgnoreResponseAdvice;
import com.kedacom.tz.sh.utils.HttpUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "测试")
@IgnoreResponseAdvice
public class TestController {

	@Autowired
	private HttpUtils httpUtils;

	@GetMapping("/test")
	@ApiOperation(value = "测试GET请求（参数放在url上）")
	public String test(String url) {
//		ResponseEntity<String> responseEntity = httpUtils.get(url, null);
		ResponseEntity<String> responseEntity = httpUtils.request(url, HttpMethod.GET, null, null);
		String body;
		if (responseEntity != null) {
			body = responseEntity.getBody();
		} else {
			body = "给定的url无效";
		}
		log.info("http status:{},response:{}", body);
		return body;
	}

}
