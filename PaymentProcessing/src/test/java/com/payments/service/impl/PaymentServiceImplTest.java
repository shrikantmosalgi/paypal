package com.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.payments.dao.TransactionDao;
import com.payments.dto.TransactionDto;
import com.payments.http.HttpServiceEngine;
import com.payments.pojo.CreatePaymentRequest;
import com.payments.pojo.PaymentResponse;
import com.payments.service.helper.PPCaptureOrderHelper;
import com.payments.service.helper.PPCreateOrderHelper;
import com.payments.services.PaymentStatusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
	
	@Mock
	private PPCreateOrderHelper ppCreateOrderHelper;
	@Mock
	private PPCaptureOrderHelper ppCaptureOrderHelper;
	@Mock
	private HttpServiceEngine httpServiceEngine;
	
	@Mock
	private PaymentStatusService paymentStatusService;
	@Mock
	private  ModelMapper modelMapper;
	@Mock
	private TransactionDao transactionDao;
	
	@InjectMocks
	PaymentServiceImpl paymentServiceImpl;
	
	@Test
	public void createPayment() {
		log.info("PaymentServiceImplTest createPaymentRequest ");

		//Arrange (input setup)
		CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
		TransactionDto response = new TransactionDto();

		when(modelMapper.map(createPaymentRequest, TransactionDto.class))
		.thenReturn(response);		
		
		when(paymentStatusService.processPayment(response)).thenReturn(response);
		//Act (call method to be tested)
		PaymentResponse txnResponse =paymentServiceImpl.createPayment(createPaymentRequest);
		
		//Assert or verify (verify the result)
		assertNotNull(txnResponse);
		assertNotNull(txnResponse.getTxnReference());
		assertNotNull(txnResponse.getTxnStatusId());
		
		assertNull(txnResponse.getRedirectUrl());
		assertNull(txnResponse.getPaypalProviderReference());
		
		assertEquals(1, txnResponse.getTxnStatusId());
		assertEquals(36, txnResponse.getTxnReference().length());

		log.info("PaymentServiceImplTest createPayment completed successfully ");
		
		
	}
}
