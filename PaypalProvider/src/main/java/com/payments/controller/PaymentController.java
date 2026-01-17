package com.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payments.pojo.CreateOrderRequest;
import com.payments.pojo.OrderResponse;
import com.payments.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/payments")
	public OrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
		log.info("inside create order controller ||createOrderRequest: {}",createOrderRequest);
		OrderResponse orderResponse =paymentService.createOrder(createOrderRequest);
		
		log.info("order created successfully || orderId: {}",orderResponse);	
		return orderResponse;
		
	}
	
	@PostMapping("/{orderId}/capture")
	public OrderResponse captureOrder(@PathVariable String orderId) {
		log.info("Capturing order in PayPal provider service"
				+ "||orderId:{}",
				orderId);
		
		OrderResponse response = paymentService.captureOrder(orderId);
		log.info("Order capture response from service: {}", response);
		
		return response;
	}
}
