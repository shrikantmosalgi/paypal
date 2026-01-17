package com.payments.service;

import com.payments.pojo.CreateOrderRequest;
import com.payments.pojo.OrderResponse;

public interface PaymentService {

	public OrderResponse createOrder(CreateOrderRequest createOrderRequest);
	public OrderResponse captureOrder(String orderId);

}
