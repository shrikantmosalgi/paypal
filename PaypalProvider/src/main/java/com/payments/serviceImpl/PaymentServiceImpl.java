package com.payments.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.payments.http.HttpRequest;
import com.payments.http.HttpServiceEngine;
import com.payments.pojo.CreateOrderRequest;
import com.payments.pojo.OrderResponse;
import com.payments.service.PaymentService;
import com.payments.service.helper.CaptureOrderHelper;
import com.payments.service.helper.CreateOrderHelper;
import com.payments.services.PaymentValidator;
import com.payments.services.TokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	@Value("${paypal.create.order.url}")
	private String createOrderUrl;
	@Autowired
	private CreateOrderHelper createOrderHelper;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private HttpServiceEngine httpServiceEngine;
	
	@Autowired
	private PaymentValidator paymentValidator;
	@Autowired
	private CaptureOrderHelper captureOrderHelper;

	@Override
	public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		log.info("creating order in PaymentServiceImpl");

		paymentValidator.validateCreateOrderRequest(createOrderRequest);

		String accessToken = tokenService.getAccessToken();

		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderHttpRequest(createOrderRequest, accessToken);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);

		OrderResponse orderResponse = createOrderHelper.handlePaypalResponse(httpResponse);
		
		log.info("order created successfully in PaymentServiceImpl orderResponse: {}", orderResponse);		
		return orderResponse;
	}

	@Override
	public OrderResponse captureOrder(String orderId) {
		log.info("Capturing order in PaymentServiceImpl|| orderId:{}",
				orderId);
		
		String accessToken = tokenService.getAccessToken();
		log.info("Access token retrieved: {}", accessToken);
		
		HttpRequest httpRequest = captureOrderHelper.prepareCaptureOrderHttpRequest(
				orderId, accessToken);
		log.info("Prepared HttpRequest for capturing order httpRequest: {}", httpRequest);
		
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP response from HttpServiceEngine: {}", httpResponse);
		
		OrderResponse orderResponse = captureOrderHelper.handlePaypalResponse(httpResponse);
		log.info("Final OrderResponse to be returned: {}", orderResponse);
		
		return orderResponse;
	}
	
	

}
