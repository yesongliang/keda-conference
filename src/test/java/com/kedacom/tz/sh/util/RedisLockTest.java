package com.kedacom.tz.sh.util;

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
}
