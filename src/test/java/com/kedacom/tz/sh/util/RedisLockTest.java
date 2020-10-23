package com.kedacom.tz.sh.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kedacom.tz.sh.ConferenceAppTest;
import com.kedacom.tz.sh.utils.RedisLock;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ConferenceAppTest.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
public class RedisLockTest {

	@Autowired
	private RedisLock redisLock;

	@Test
	public void lockTest() {
		String vlaue = redisLock.fetchLockValue();
		Boolean lock = redisLock.lock("ysl", vlaue);
		log.info("vlaue={},lock={}", vlaue, lock);
	}

	@Test
	public void unLockTest() {
		Boolean unLock = redisLock.unLock("ysl", "b1c961a2-5e06-4ebf-8a6a-a39a9d916e36_1599475749226");
		log.info("unLock={}", unLock);
	}

	@Test
	public void rateLimitTest() throws InterruptedException {
		String key = "test_rateLimit_key";
		int max = 10; // 令牌桶大小
		int rate = 10; // 令牌每秒恢复速度
		AtomicInteger successCount = new AtomicInteger(0);
		Executor executor = Executors.newFixedThreadPool(10);
		CountDownLatch countDownLatch = new CountDownLatch(30);
		for (int i = 0; i < 30; i++) {
			executor.execute(() -> {
				boolean isAllow = redisLock.rateLimit(key, max, rate);
				if (isAllow) {
					successCount.addAndGet(1);
				}
				log.info(Boolean.toString(isAllow));
				countDownLatch.countDown();
			});
		}
		countDownLatch.await();
		log.info(" 请求成功{}次 ", successCount.get());
	}

}
