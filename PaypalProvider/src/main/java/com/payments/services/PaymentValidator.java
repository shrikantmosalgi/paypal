package com.payments.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.payments.constant.ErrorCodeEnum;
import com.payments.exception.PaypalProviderException;
import com.payments.pojo.CreateOrderRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentValidator {

	public void validateCreateOrderRequest(CreateOrderRequest createOrderRequest) {
		log.info("Validating CreateOrderRequest: {}", createOrderRequest);

		if (createOrderRequest == null) {
			log.error("CreateOrderRequest is null");
			throw new PaypalProviderException(ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
					ErrorCodeEnum.INVALID_REQUEST.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		if (createOrderRequest.getAmount() == null || createOrderRequest.getAmount() <= 0) {
			log.error("Invalid amount: {}", createOrderRequest.getAmount());
			throw new PaypalProviderException(ErrorCodeEnum.INVALID_AMOUNT.getErrorCode(),
					ErrorCodeEnum.INVALID_AMOUNT.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}
		if (createOrderRequest.getCurrencyCode() == null || createOrderRequest.getCurrencyCode().isBlank()) {
			log.error("Currency code is required and cannot be blank");
			throw new PaypalProviderException(ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getErrorCode(),
					ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}
		if (createOrderRequest.getReturnUrl() == null || createOrderRequest.getReturnUrl().isBlank()) {
			log.error("Return URL is required and cannot be blank");
			throw new PaypalProviderException(ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorCode(),
					ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}
		if (createOrderRequest.getCancelUrl() == null || createOrderRequest.getCancelUrl().isBlank()) {
			log.error("Cancel URL is required and cannot be blank");
			throw new PaypalProviderException(ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorCode(),
					ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

	}
}
