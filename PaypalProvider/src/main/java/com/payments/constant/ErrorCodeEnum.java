package com.payments.constant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("30000", "Something went wrong,Please try again later"),
	CURRENCY_CODE_REQUIRED("30001", "Currency code is required and cannot be blank"),
	RETURN_URL_REQUIRED("30002", "Return URL is required and cannot be blank"),	
	INVALID_REQUEST("30003", "The request is invalid"),
	INVALID_AMOUNT("30004", "The amount must be greater than zero"),
	CANCEL_URL_REQUIRED("30005", "Cancel URL is required and cannot be blank"),
	PAYPAL_SERVICE_UNAVAILABLE("30006", "Paypal service is currently unavailable, please try again later"),
	PAYPAL_ERROR("30007", "<Error at Paypal side>"),
	PAYPAL_UNKNOWN_ERROR("30008", "An unknown error occurred at Paypal side"),
    RESOURCE_NOT_FOUND("30009", "Invalid URL. Please check and try again.");

    private final String errorCode;
    private final String errorMessage;
    
	
}
