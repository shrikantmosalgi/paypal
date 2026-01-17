package com.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payments.pojo.CreatePaymentRequest;
import com.payments.pojo.InitiatePaymentRequest;
import com.payments.pojo.PaymentResponse;
import com.payments.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

	@Autowired
	private  PaymentService paymentService;

	@PostMapping
	public PaymentResponse createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
		log.info("Creating payment... ||createPaymentRequest:{}", createPaymentRequest);

		PaymentResponse response = paymentService.createPayment(createPaymentRequest);
		log.info("Payment creation response from service: {}", response);

		return response;
	}

	@PostMapping("/{txnReference}/initiate")
	public PaymentResponse initiatePayment(@PathVariable String txnReference,
			@RequestBody InitiatePaymentRequest initiatePaymentRequest) {
		log.info("Initiating payment... txnReference: {}| initiatePaymentRequest:{}", txnReference,
				initiatePaymentRequest);

		PaymentResponse response = paymentService.initiatePayment(txnReference, initiatePaymentRequest);
		log.info("Payment initiation response from service: {}", response);

		return response;
	}

	@PostMapping("/{txnReference}/capture")
	public PaymentResponse capturePayment(@PathVariable String txnReference) {
		log.info("Capturing payment... txnReference: {}", txnReference);

		PaymentResponse response = paymentService.capturePayment(txnReference);
		log.info("Payment capture response from service: {}", response);

		return response;
	}

}
