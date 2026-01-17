package com.payments.constant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("20000", "Something went wrong,Please try again later"),
    RESOURCE_NOT_FOUND("20001", "Invalid URL. Please check and try again."),
	PAYPAL_PROVIDER_SERVICE_UNAVAILABLE("20002", "Paypal-Provider service is currently unavailable, please try again later"),
	NO_STATUS_PROCESSOR_FOUND("20003", "No Status Processor found"),
	PAYPAL_PROVIDER_UNKNOWN_ERROR("20004", "Unknown error occurred in Paypal-Provider service"),
	ERROR_UPDATING_TRANSACTION("20005", "Error updating transaction details");
	
    private final String errorCode;
    private final String errorMessage;
    
	private ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
    
	
}
