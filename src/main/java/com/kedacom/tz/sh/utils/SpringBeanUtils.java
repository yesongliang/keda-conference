package com.kedacom.tz.sh.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring IOC上下文工具类
 * 
 * @author ysl
 */
@Component
public class SpringBeanUtils implements ApplicationContextAware {

	/**
	 * 当前IOC
	 */
	private static ApplicationContext applicationContext;

	/**
	 * 设置当前上下文环境，此方法由spring自动装配
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		applicationContext = arg0;
	}

	/**
	 * 从当前IOC获取bean
	 * 
	 * @param beanId
	 *            bean的id
	 * @return T
	 */
	public static <T> T getBean(String beanId) {
		@SuppressWarnings("unchecked")
		T t = (T) applicationContext.getBean(beanId);
		return t;
	}

	public static <T> T getBeanByClass(Class<T> clazz) {
		return applicationContext == null ? null : applicationContext.getBean(clazz);
	}

}