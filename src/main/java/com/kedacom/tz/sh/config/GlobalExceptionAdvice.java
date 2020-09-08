package com.kedacom.tz.sh.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kedacom.tz.sh.exception.BusinessException;
import com.kedacom.tz.sh.vo.CommonResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一自定义异常处理
 * 
 * @author ysl
 *
 */
@RestControllerAdvice(basePackages = "com.kedacom.tz.sh.controller")
@Slf4j
public class GlobalExceptionAdvice {

	@ExceptionHandler(value = BusinessException.class)
	public CommonResponse<String> handerAdException(HttpServletRequest req, BusinessException ex) {
		String servletPath = req.getServletPath();
		log.error("业务处理产生异常,request_url={},error_message={}", servletPath, ex.getMessage());
		// 500:业务异常
		CommonResponse<String> response = new CommonResponse<>(500, ex.getMessage());
		return response;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonResponse<String> validationError(HttpServletRequest req, MethodArgumentNotValidException ex) {
		String servletPath = req.getServletPath();
		FieldError fieldError = ex.getBindingResult().getFieldError();
		log.error("请求参数错误,request_url={},error_message={}", servletPath, fieldError.getField() + fieldError.getDefaultMessage());
		// 400:参数错误
		CommonResponse<String> response = new CommonResponse<>(400, fieldError.getField() + fieldError.getDefaultMessage());
		return response;
	}
}
