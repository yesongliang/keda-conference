package com.kedacom.tz.sh.utils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.kedacom.tz.sh.thread.ConferencePlatform;

/**
 * 会议平台管理类
 * 
 * @author ysl
 *
 */
public class ConferencePlatformManager {

	// TODO 设置支持的最大容量
	private static final int capacity = 32;

	private static Map<Long, ConferencePlatform> platform = new ConcurrentHashMap<>();

	private static Lock lock = new ReentrantLock();

	// TODO 线程为死循环执行，队列设置容量为零（此种情况是否有必要使用线程池调度线程？）
	private static Executor executor = new ThreadPoolExecutor(1, capacity, 60, TimeUnit.SECONDS, new SynchronousQueue<>());

	/**
	 * 添加单个会议平台
	 * 
	 * @param key
	 * @param value
	 */
	public static AddType addPlatform(Long key, ConferencePlatform value) {
		lock.lock();
		try {
			// 是否已存在
			if (isExist(key)) {
				return AddType.EXIST;
			}
			// 是否超出最大容量
			if (platform.size() < capacity) {
				// 加入
				platform.put(key, value);
				// 执行
				executor.execute(value);
				return AddType.SUCCESS;
			}
			return AddType.OUTOFSIZE;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取单个会议平台
	 * 
	 * @param key
	 * @return
	 */
	public static ConferencePlatform getPlatform(Long key) {
		return platform.get(key);
	}

	// 外部先调用removePlatformForOut函数，将线程设置跳出死循环
	/**
	 * 移除单个会议平台,对外提供调用
	 * 
	 * @param key
	 */
	public static void removePlatformForOut(Long key) {
		ConferencePlatform conferencePlatform = platform.get(key);
		if (conferencePlatform != null) {
			conferencePlatform.setRunning(false);
		}
	}

	// 线程内部调用移除
	/**
	 * 移除单个会议平台,对内提供调用
	 * 
	 * @param key
	 */
	public static void removePlatformForIn(Long key) {
		platform.remove(key);
	}

	/**
	 * 判度是否存在key的会议平台
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isExist(Long key) {
		boolean containsKey = platform.containsKey(key);
		return containsKey;
	}

	public static Collection<ConferencePlatform> getPlatforms() {
		Collection<ConferencePlatform> values = platform.values();
		return values;
	}

	public static int getCapacity() {
		return capacity;
	}

	/**
	 * 添加操作返回枚举类
	 * 
	 * @author ysl
	 *
	 */
	public enum AddType {
		EXIST("exist", "已存在"), OUTOFSIZE("out_of_size", "超出容量"), SUCCESS("success", "成功"),

		;

		private String type;
		private String describe;

		private AddType(String type, String describe) {
			this.type = type;
			this.describe = describe;
		}

		public String getType() {
			return type;
		}

		public String getDescribe() {
			return describe;
		}

	}

}
