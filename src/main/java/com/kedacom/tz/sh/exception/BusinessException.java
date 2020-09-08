package com.kedacom.tz.sh.exception;

/**
 * 自定义异常
 * 
 * @author ysl
 *
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -1676123417417930380L;

	public BusinessException(String message) {
		super(message);
	}

}
