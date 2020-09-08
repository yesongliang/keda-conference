package com.kedacom.tz.sh.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池
 * 
 * @author songliang.ye
 *
 */
//TODO 目前用于运行平台维护线程
@Configuration
//@EnableAsync
public class ExecutorConfig {

	/**
	 * @Desc 使用注解@Async("asyncExecutor")标明异步方法将进入asyncExecutor线程池中执行
	 *       注解@Async后加不加asyncExecutor参数都会使用这个线程池执行
	 */
	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 配置核心线程数
		executor.setCorePoolSize(3);
		// 配置最大线程数
		executor.setMaxPoolSize(200);
		// 配置队列大小
		executor.setQueueCapacity(0);
		// 配置线程池中的线程的名称前缀
		executor.setThreadNamePrefix("async-call-");

		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 执行初始化
		executor.initialize();
		return executor;
	}

}
