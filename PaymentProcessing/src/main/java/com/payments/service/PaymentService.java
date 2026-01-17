package com.payments.service;

import com.payments.pojo.CreatePaymentRequest;
import com.payments.pojo.InitiatePaymentRequest;
import com.payments.pojo.PaymentResponse;

public interface PaymentService {

	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);
	public PaymentResponse initiatePayment(String txnReference,InitiatePaymentRequest initiatePaymentRequest);
	public PaymentResponse capturePayment(String txnReference);
}
