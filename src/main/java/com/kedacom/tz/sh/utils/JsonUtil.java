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
	 * 通过属性的set方法给对象设置
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
//			String type = field.getGenericType().toString();
			// 获取set方法名称
			String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			Class<?> methodClass = field.getType();
			Object value = jsonObject.get(name);
//			if ("class java.lang.String".equals(type)) {
//				value = jsonObject.getString(name);
//				methodClass = String.class;
//			} else if ("class java.lang.Integer".equals(type)) {
//				value = jsonObject.getInteger(name);
//				methodClass = Integer.class;
//			}
//			// 后续可支持更多的类型
			clz.getMethod(methodName, methodClass).invoke(data, value);
		}
		return data;
	}
}
