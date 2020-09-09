package com.kedacom.tz.sh.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.alibaba.fastjson.JSONObject;

/**
 * 业务JSON解析工具
 * 
 * @author ysl
 *
 */
public class JsonUtil {

	/**
	 * 解析JSON数据构建实体对象
	 * 
	 * @param <T>
	 * @param clz
	 * @param jsonObject
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static <T> T analysis(Class<T> clz, JSONObject jsonObject)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		T data = clz.newInstance();
		Field[] declaredFields = clz.getDeclaredFields();
		for (Field field : declaredFields) {
			String name = field.getName();
			String type = field.getGenericType().toString();
			String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			Class<?> methodClass = null;
			Object value = null;
			if ("class java.lang.String".equals(type)) {
				value = jsonObject.getString(name);
				methodClass = String.class;
			} else if ("class java.lang.Integer".equals(type)) {
				value = jsonObject.getInteger(name);
				methodClass = Integer.class;
			}
			clz.getMethod(methodName, methodClass).invoke(data, value);
		}
		return data;
	}
}
