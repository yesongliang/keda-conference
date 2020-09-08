package com.kedacom.tz.sh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 注解，标识不使用CommonResponse统一响应数据格式封装
 * 
 * @author ysl
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResponseAdvice {
}
