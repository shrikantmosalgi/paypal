package com.payments.service.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.payments.constant.ErrorCodeEnum;
import com.payments.dto.TransactionDto;
import com.payments.exception.ProcessingServiceException;
import com.payments.http.HttpRequest;
import com.payments.paypalprovider.PPCreateOrderRequest;
import com.payments.paypalprovider.PPErrorResponse;
import com.payments.paypalprovider.PPOrderResponse;
import com.payments.pojo.InitiatePaymentRequest;
import com.payments.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PPCreateOrderHelper {

	@Autowired
	private JsonUtil jsonUtil;
	@Value("${paypalprovider.create.order.url}")
	private String payPalProviderCreateOrderUrl;

	public HttpRequest prepareHttpRequest(String txnReference, InitiatePaymentRequest initiatePaymentRequest,
			TransactionDto transactionDto) {

		log.info(
				"Preparing HttpRequest for PayPal Create Order for txnReference: {} | "
						+ "initiatePaymentRequest: {} | transactionDto: {} ",
				txnReference, initiatePaymentRequest, transactionDto);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		PPCreateOrderRequest ppCreateOrderRequest = new PPCreateOrderRequest();
		ppCreateOrderRequest.setAmount(transactionDto.getAmount().doubleValue());
		ppCreateOrderRequest.setCurrencyCode(transactionDto.getCurrency());
		ppCreateOrderRequest.setReturnUrl(initiatePaymentRequest.getSuccessUrl());
		ppCreateOrderRequest.setCancelUrl(initiatePaymentRequest.getCancelUrl());

		String requestAsJson = jsonUtil.convertObjectToJson(ppCreateOrderRequest);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(payPalProviderCreateOrderUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(requestAsJson);
		log.info("Prepared HttpRequest for PayPal Create Order: {} ", httpRequest);

		return httpRequest;
	}

	public PPOrderResponse processResponse(ResponseEntity<String> httpResponse) {

		log.info("Processing PayPal Create Order response: {} ", httpResponse);
		if (httpResponse.getStatusCode().equals(HttpStatus.OK)) {
			log.info("PayPal Create Order successful with response: {} ", httpResponse.getBody());

			PPOrderResponse responseObj = jsonUtil.convertJsonToObject(httpResponse.getBody(), PPOrderResponse.class);
			if (responseObj != null && responseObj.getOrderId() != null && responseObj.getPaypalStatus() != null
					&& responseObj.getRedirectUrl() != null) {
				log.info("PayPal Create Order response : {} ", responseObj);
				return responseObj;
			} else {
				log.error("Failed to parse PayPal Create Order response: {} ", httpResponse.getBody());
			}
		}

		if (httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError()) {

			log.error("PayPal Create Order failed with response: {} ", httpResponse.getBody());
			PPErrorResponse errorResponse = jsonUtil.convertJsonToObject(httpResponse.getBody(), PPErrorResponse.class);
			throw new ProcessingServiceException(errorResponse.getErrorCode(), errorResponse.getErrorMessage(),
					HttpStatus.valueOf(httpResponse.getStatusCode().value()));
		}

		log.error("Unknown error occurred during PayPal Create Order with response: {} ", httpResponse);

		throw new ProcessingServiceException(ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorMessage(), HttpStatus.BAD_GATEWAY);

	}

}