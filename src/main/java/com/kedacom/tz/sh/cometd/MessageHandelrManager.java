package com.kedacom.tz.sh.cometd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.kedacom.tz.sh.cometd.handler.ChairmanHandler;
import com.kedacom.tz.sh.cometd.handler.ConfInfoHandler;
import com.kedacom.tz.sh.cometd.handler.DualstreamHandler;
import com.kedacom.tz.sh.cometd.handler.InitHandler;
import com.kedacom.tz.sh.cometd.handler.InspectionHandler;
import com.kedacom.tz.sh.cometd.handler.MixHandler;
import com.kedacom.tz.sh.cometd.handler.MonitorHandler;
import com.kedacom.tz.sh.cometd.handler.MtListHandler;
import com.kedacom.tz.sh.cometd.handler.SpeakerHandler;
import com.kedacom.tz.sh.cometd.handler.VmpHandler;
import com.kedacom.tz.sh.utils.IPUtils;
import com.kedacom.tz.sh.utils.SpringBeanUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 一个会议平台的消息一个线程消费，消费策略：公平，先来先消费
 * 
 * @author ysl
 *
 */
@Slf4j
public class MessageHandelrManager {

	private Map<Long, MessageHandleThread> threadMap = new ConcurrentHashMap<Long, MessageHandleThread>();

	// 阻塞队列，先进先出
	private Map<Long, LinkedBlockingQueue<Map<String, String>>> queueMap = new ConcurrentHashMap<Long, LinkedBlockingQueue<Map<String, String>>>();

	// TODO 设置支持的最大容量
	private int capacity = 32;

	// TODO 线程为死循环执行，队列设置容量为零（此种情况是否有必要使用线程池调度线程？）
	private Executor executor = new ThreadPoolExecutor(1, capacity, 60, TimeUnit.SECONDS, new SynchronousQueue<>());

	private static MessageHandelrManager instance = new MessageHandelrManager();

	private MessageHandelrManager() {

	}

	public static MessageHandelrManager getInstance() {
		return instance;
	}

	public void addThread(Long platformId) {
		boolean containsKey = this.threadMap.containsKey(platformId);
		if (!containsKey) {
			MessageHandleThread messageHandleThread = this.new MessageHandleThread(platformId);
			this.threadMap.put(platformId, messageHandleThread);
			LinkedBlockingQueue<Map<String, String>> linkedBlockingQueue = new LinkedBlockingQueue<>(this.capacity);
			this.queueMap.put(platformId, linkedBlockingQueue);
			// TODO 运行线程
//			Thread t = new Thread(messageHandleThread);
//			t.start();
			this.executor.execute(messageHandleThread);
		}
	}

	public void removeThread(Long platformId) {
		MessageHandleThread messageHandleThread = this.threadMap.get(platformId);
		if (messageHandleThread != null) {
			messageHandleThread.setRunning(false);
			this.threadMap.remove(platformId);
			this.queueMap.remove(platformId);
		}
	}

	/**
	 * 
	 * @param platformId
	 * @param map
	 */
	public void addMessage(Long platformId, Map<String, String> map) {
		LinkedBlockingQueue<Map<String, String>> linkedBlockingQueue = this.queueMap.get(platformId);
		if (linkedBlockingQueue == null) {
			return;
		}
		boolean offer = linkedBlockingQueue.offer(map);
		if (!offer) {
			// 1、数据量大导致；2、消费线程异常
			log.error("消息队列消息溢出，丢失消息:{}", map);
		}
	}

	@Data
	private class MessageHandleThread implements Runnable {

		/** 会议平台唯一标识 **/
		private Long platformId;

		/** 运行 **/
		private boolean running = true;

		public MessageHandleThread(Long platformId) {
			this.platformId = platformId;
		}

		@Override
		public void run() {
			log.debug("会议平台消息消费线程---start---，ip={}", IPUtils.longToIP(this.platformId));
			while (running) {
				try {
					LinkedBlockingQueue<Map<String, String>> linkedBlockingQueue = queueMap.get(this.platformId);
					if (linkedBlockingQueue == null) {
						break;
					}
					Map<String, String> poll = linkedBlockingQueue.poll(10, TimeUnit.SECONDS);
					if (poll == null) {
						continue;
					}
					IHandler handle = getHandle(poll.get("type"));
					if (handle != null) {
						handle.handle(poll);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// 业务异常
					e.printStackTrace();
				}
			}
			log.debug("会议平台消息消费线程---end---，ip={}", IPUtils.longToIP(this.platformId));
		}

		/**
		 * 获取处理器
		 * 
		 * @param type 消息类型
		 * @return
		 */
		private IHandler getHandle(String type) {
			IHandler handle = null;
			switch (type) {
			case "conf_info":
				handle = SpringBeanUtils.getBeanByClass(ConfInfoHandler.class);
				break;
			case "mt_list":
				handle = SpringBeanUtils.getBeanByClass(MtListHandler.class);
				break;
			case "mix":
				handle = SpringBeanUtils.getBeanByClass(MixHandler.class);
				break;
			case "vmp":
				handle = SpringBeanUtils.getBeanByClass(VmpHandler.class);
				break;
			case "speaker":
				handle = SpringBeanUtils.getBeanByClass(SpeakerHandler.class);
				break;
			case "chairman":
				handle = SpringBeanUtils.getBeanByClass(ChairmanHandler.class);
				break;
			case "dualstream":
				handle = SpringBeanUtils.getBeanByClass(DualstreamHandler.class);
				break;
			case "inspection":
				handle = SpringBeanUtils.getBeanByClass(InspectionHandler.class);
				break;
			case "monitor":
				handle = SpringBeanUtils.getBeanByClass(MonitorHandler.class);
				break;
			case "subScribe":
				handle = SpringBeanUtils.getBeanByClass(InitHandler.class);
				break;
			default:
				break;
			}
			return handle;
		}

	}
}
