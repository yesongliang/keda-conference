package com.kedacom.tz.sh.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * redis分布式锁
 * 
 * @author songliang.ye
 *
 */
@Slf4j
@Component
public class RedisLock {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private DefaultRedisScript<Long> ratelimitLua;

	/** 执行结果标识 **/
	private static final int SUCCESS = 1;
	/** 锁的过期时间（单位：秒） **/
	private static final int LOCK_EXPIRE_TIME = 30;
	/** key的前缀 **/
	private static final String KEY_PRE = "REDIS_LOCK:";
	/** 默认尝试次数 **/
	private static final int DEFAULT_ATTEMPTS = 10;

	/**
	 * 加锁
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Boolean lock(String key, String value) {
		return tryLock(key, value, DEFAULT_ATTEMPTS);
	}

	/**
	 * 尝试加锁（不可重入锁）
	 * 
	 * @param key
	 * @param value
	 * @param maxAttempts
	 * @return
	 */
	public Boolean tryLock(String key, String value, int maxAttempts) {
		int attemptCounter = 0;
		key = KEY_PRE + key;
		while (attemptCounter < maxAttempts) {
			// 原子操作
			Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(key, value, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
			if (setIfAbsent) {
				return Boolean.TRUE;
			}
			attemptCounter++;
			if (attemptCounter >= maxAttempts) {
				log.error("获取锁失败,key:{}", key);
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				log.error("获取锁等待异常,key:{}", e.getMessage());
				e.printStackTrace();
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * 解锁
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Boolean unLock(String key, String value) {
		key = KEY_PRE + key;
		String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
		RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
		List<String> keys = Arrays.asList(key);
		// 原子操作（Lua脚本）
		Long execute = redisTemplate.execute(redisScript, keys, value);
		if (SUCCESS == execute.intValue()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 生成唯一字符串
	 * 
	 * @return
	 */
	public String fetchLockValue() {
		return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
	}

	// 令牌限流算法实现
	public boolean rateLimit(String key, int max, int rate) {
		Long res = redisTemplate.execute((RedisConnection connection) -> connection.eval(ratelimitLua.getScriptAsString().getBytes(), ReturnType.INTEGER, 1, key.getBytes(),
				Integer.toString(max).getBytes(), Integer.toString(rate).getBytes(), Long.toString(System.currentTimeMillis()).getBytes()));
		return 1 == res;
		// TODO 以下方式获取不到传递的参数
//		List<String> keyList = new ArrayList<>(1);
//		keyList.add(key);
//		return 1 == redisTemplate.execute(ratelimitLua, keyList, Integer.toString(max), Integer.toString(rate), Long.toString(System.currentTimeMillis()));
	}

}
