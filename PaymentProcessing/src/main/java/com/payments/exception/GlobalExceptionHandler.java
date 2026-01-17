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

	/* when we throw PaypalProviderException from anywhere in the application this will come to this class
	 *  because of @RestControllerAdvice 
	 *  */
	
	@ExceptionHandler(ProcessingServiceException.class)
    public ResponseEntity<ErrorResponse> handlePaypalException(ProcessingServiceException ex) {
		
		log.error("Exception occurred: {}",ex);
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode(),
                ex.getErrorMessage()
        );

        return new ResponseEntity<>(response,ex.getHttpStatus());
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		
		log.error("Exception occurred: {}",ex);
        ErrorResponse response = new ErrorResponse(
                ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
                ErrorCodeEnum.GENERIC_ERROR.getErrorMessage()
        );

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> resourceNotFoundException(NoResourceFoundException exception) {

		log.error("NoResourceFoundException occurred: {}",exception);
		ErrorResponse errorResponse = new ErrorResponse(ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorCode(),
				ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorMessage());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
}
