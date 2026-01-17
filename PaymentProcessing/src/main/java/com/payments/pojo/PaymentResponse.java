package com.payments.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PaymentResponse {

	private String txnReference;
	private int txnStatusId;
	private String redirectUrl;
	private String paypalProviderReference;
}
