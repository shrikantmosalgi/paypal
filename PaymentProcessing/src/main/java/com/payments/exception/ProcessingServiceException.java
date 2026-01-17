package com.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ProcessingServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4971194311021208051L;
	private String errorCode;
	private String errorMessage;
	private HttpStatus httpStatus;

	public ProcessingServiceException(String errorCode, String errorMessage,HttpStatus httpStatus) {
		
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus;
	}
}
