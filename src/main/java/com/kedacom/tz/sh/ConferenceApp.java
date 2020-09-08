package com.kedacom.tz.sh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
//启动熔断机制
@EnableCircuitBreaker
//@EnableEurekaClient
public class ConferenceApp {
	public static void main(String[] args) {
		SpringApplication.run(ConferenceApp.class, args);
	}
}
