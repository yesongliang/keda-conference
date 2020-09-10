package com.kedacom.tz.sh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

/**
 * TODO
 * 
 * 1、一个会议平台需要两个线程，一个维护状态，一个消费消息
 * 
 * 2、是透传还是维护会议数据？（计划：对数据进行整理，维护一份简化版会议数据）
 * 
 * @author ysl
 *
 */
@SpringBootApplication
//启动熔断机制
@EnableCircuitBreaker
//@EnableEurekaClient
public class ConferenceApp {
	public static void main(String[] args) {
		SpringApplication.run(ConferenceApp.class, args);
	}
}
