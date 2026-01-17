package com.payments.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.payments.constant.ErrorCodeEnum;
import com.payments.dao.TransactionDao;
import com.payments.dto.TransactionDto;
import com.payments.entity.Transaction;
import com.payments.exception.ProcessingServiceException;
import com.payments.http.HttpRequest;
import com.payments.http.HttpServiceEngine;
import com.payments.paypalprovider.PPOrderResponse;
import com.payments.pojo.CreatePaymentRequest;
import com.payments.pojo.InitiatePaymentRequest;
import com.payments.pojo.PaymentResponse;
import com.payments.service.PaymentService;
import com.payments.service.helper.PPCaptureOrderHelper;
import com.payments.service.helper.PPCreateOrderHelper;
import com.payments.services.PaymentStatusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PPCreateOrderHelper ppCreateOrderHelper;
	@Autowired
	private PPCaptureOrderHelper ppCaptureOrderHelper;
	@Autowired
	private HttpServiceEngine httpServiceEngine;
	
	@Autowired
	private PaymentStatusService paymentStatusService;
	@Autowired
	private  ModelMapper modelMapper;
	@Autowired
	private TransactionDao transactionDao;
	
	@Override
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("Creating payment in PaymentServiceImpl createPaymentRequest: {} ", createPaymentRequest);
		
		
		TransactionDto transactionDto =modelMapper.map(createPaymentRequest, TransactionDto.class);
		
		int txnStatusId =1; // CREATED
		String txnReference = generateUniqueTxnRefrence();
		transactionDto.setTxnStatusId(txnStatusId);
		transactionDto.setTxnReference(txnReference);
		TransactionDto response = paymentStatusService.processPayment(transactionDto);

		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(response.getTxnReference());
		paymentResponse.setTxnStatusId(response.getTxnStatusId());
		
		log.info("Payment created with response: {} ", paymentResponse);
		return paymentResponse;
	}

	private String generateUniqueTxnRefrence() {
		String txnReference =UUID.randomUUID().toString();
		return txnReference;
	}

	@Override
	public PaymentResponse initiatePayment(String txnReference,InitiatePaymentRequest initiatePaymentRequest) {

		log.info("Initiating payment in PaymentServiceImpl... "
				+ "txnReference: {} | initiatePaymentRequest:{}", 
				txnReference, initiatePaymentRequest);
		
		Transaction txnEntity = transactionDao.getTransactionByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from DB: {}", txnEntity);
		
		// use modelMapper to convert Entity to DTO
		TransactionDto txnDto = modelMapper.map(
				txnEntity, TransactionDto.class);
		log.info("Mapped TransactionEntity to TransactionDto: {}", txnDto);
		
		// update txn status to INITIATED
		txnDto.setTxnStatusId(2); // INITIATED
		txnDto = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after updating status to INITIATED: {}", txnDto);
		
		// MAKE API CALL To payal-provider to createOrder API
		/*
		 * 1. Prepare HttpRequest - DONE
		 * 2. Pass to HttpServiceEngine
		 * 3. Process the response
		 */

		HttpRequest httpReq = ppCreateOrderHelper.prepareHttpRequest(
				txnReference, initiatePaymentRequest, txnDto);
		log.info("Prepared HttpRequest for PayPalProvider create order: {}", httpReq);

		PPOrderResponse ppOrderSuccessResponse = null;
		try {
			ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpReq);
			log.info("HTTP response from HttpServiceEngine: {}", httpResponse);
			
			ppOrderSuccessResponse = ppCreateOrderHelper.processResponse(httpResponse);
			log.info("Processed PayPal order response: {}", ppOrderSuccessResponse);
		} catch (ProcessingServiceException e) {
			log.error("Error occurred while making HTTP call to PayPalProvider: ", e);
			
			// update txn status to FAILED
			txnDto.setTxnStatusId(6); // FAILED
			txnDto.setErrorCode(e.getErrorCode());
			txnDto.setErrorMessage(e.getErrorMessage());
			
			paymentStatusService.processPayment(txnDto);
			log.info("Updated transaction status to FAILED for txnReference: {}", txnReference);
			
			throw e; // rethrow the exception after updating status
		}catch (Exception e) {
			log.error("Error occurred while making HTTP call: ", e);
			txnDto.setTxnStatusId(6); // FAILED
			txnDto.setErrorCode(ErrorCodeEnum.GENERIC_ERROR.getErrorCode());
			txnDto.setErrorMessage(ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());
			paymentStatusService.processPayment(txnDto);
			throw e;
			
		}
		
		
		
		// update txn status to PENDING
		txnDto.setTxnStatusId(3); // PENDING
		txnDto.setProviderReference(ppOrderSuccessResponse.getOrderId());
		txnDto = paymentStatusService.processPayment(txnDto);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(txnDto.getTxnReference());
		paymentResponse.setTxnStatusId(txnDto.getTxnStatusId());
		paymentResponse.setPaypalProviderReference(ppOrderSuccessResponse.getOrderId());
		paymentResponse.setRedirectUrl(ppOrderSuccessResponse.getRedirectUrl());

		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
		
		return paymentResponse;
	}

	@Override
	public PaymentResponse capturePayment(String txnReference) {

		log.info("Capturing payment in PaymentServiceImpl... "
				+ "txnReference: {}", txnReference);
		
		Transaction txnEntity = transactionDao.getTransactionByTxnReference(
				txnReference);
		log.info("Fetched TransactionEntity from DB: {}", txnEntity);
		
		// use modelMapper to convert Entity to DTO
		TransactionDto txnDto = modelMapper.map(
				txnEntity, TransactionDto.class);
		log.info("Mapped TransactionEntity to TransactionDto: {}", txnDto);
		
		// update txn status to APPROVED
		txnDto.setTxnStatusId(4);  
		txnDto = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after updating status to APPROVED: {}", txnDto);
		
		HttpRequest httpReq = ppCaptureOrderHelper.prepareHttpRequest(
				txnReference, txnDto);
		
		PPOrderResponse ppCaptureOrderSuccessResponse = null;
		try {
			ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpReq);
			log.info("HTTP response from HttpServiceEngine: {}", httpResponse);
			
			ppCaptureOrderSuccessResponse = ppCaptureOrderHelper.processResponse(httpResponse);
			log.info("Processed PayPal order response: {}", ppCaptureOrderSuccessResponse);
		} catch (Exception e) {
			log.error("Error occurred while making captureOrder HTTP call to PayPalProvider: ", e);

			throw e; // rethrow the exception after updating status
		} 
		
		// update txn status to SUCCESS
		txnDto.setTxnStatusId(5); 
		
		txnDto.setErrorMessage(null);
		txnDto.setErrorCode(null);
		txnDto = paymentStatusService.processPayment(txnDto);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(txnDto.getTxnReference());
		paymentResponse.setTxnStatusId(txnDto.getTxnStatusId());

		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
		
		return paymentResponse;
	
	}

}
