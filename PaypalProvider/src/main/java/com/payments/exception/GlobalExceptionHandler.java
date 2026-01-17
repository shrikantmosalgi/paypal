package com.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.payments.constant.ErrorCodeEnum;
import com.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/*
	 * when we throw PaypalProviderException from anywhere in the application this
	 * will come to this class because of @RestControllerAdvice
	 */

	@ExceptionHandler(PaypalProviderException.class)
	public ResponseEntity<ErrorResponse> handlePaypalException(PaypalProviderException ex) {

		log.error("PaypalProviderException occurred: {}", ex.getMessage(), ex);
		ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

		return new ResponseEntity<>(response, ex.getHttpStatus());
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
		log.error("Handling NoResourceFoundException: {}", ex.getMessage(), ex);

		ErrorResponse error = new ErrorResponse(ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorCode(),
				ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorMessage());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {

		log.error("Exception occurred: {}", ex.getMessage(), ex);
		ErrorResponse response = new ErrorResponse(ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
				ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
