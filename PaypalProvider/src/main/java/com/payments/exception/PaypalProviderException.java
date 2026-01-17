package com.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class PaypalProviderException extends RuntimeException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8825749222291610849L;	
	private String errorCode;
	private String errorMessage;
	private HttpStatus httpStatus;

	public PaypalProviderException(String errorCode, String errorMessage,HttpStatus httpStatus) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus;
	}
}
