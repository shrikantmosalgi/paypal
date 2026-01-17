package com.payments.pojo;

import lombok.Data;

@Data
public class CreateOrderRequest {

	private String currencyCode;
	private Double amount;
	private String returnUrl;
	private String cancelUrl;
}
