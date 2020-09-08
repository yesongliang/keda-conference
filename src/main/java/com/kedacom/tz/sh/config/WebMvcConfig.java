package com.kedacom.tz.sh.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 解决静态资源无法访问
		registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/static/");
		// 解决swagger无法访问
		registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		// 解决swagger的js文件无法访问
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	// 定义消息转换器，默认为StringHttpMessageConverter
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.clear();
//		converters.add(new MappingJackson2HttpMessageConverter());
		converters.add(new FastJsonHttpMessageConverter());
	}

}
