package com.kedacom.tz.sh.config;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import okhttp3.OkHttpClient;

/**
 * 开启RestTemplate支持(构建HTTP请求实例)
 * 
 * @author songliang.ye
 *
 */
@Configuration
public class RestTemplateConfig {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(/* RestTemplateBuilder builder */) {
		// 默认使用httpClient
//		return builder.setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
		// 使用okHttp
		OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS).build();
		// 构造函数一
		OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
		// 构造函数二
//		OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();
//		okHttp3ClientHttpRequestFactory.setConnectTimeout(5000);
//		okHttp3ClientHttpRequestFactory.setReadTimeout(5000);
		RestTemplate restTemplate = new RestTemplate(okHttp3ClientHttpRequestFactory);
		// 解决中文乱码，解决方案思路都是将ISO-8859-1的StringHttpMessageConverter替换为UTF-8的StringHttpMessageConverter
		// 方法一
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		// 方法二
//		for (HttpMessageConverter<?> httpMessageConverter : restTemplate.getMessageConverters()) {
//			if (httpMessageConverter instanceof StringHttpMessageConverter) {
//				((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(StandardCharsets.UTF_8);
//			}
//		}
		return restTemplate;
	}

}
