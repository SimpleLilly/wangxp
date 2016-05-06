package com.funtalk.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * 
 * @author xuyadong
 *
 */
public class BusinessException extends NestedRuntimeException {


	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

}
