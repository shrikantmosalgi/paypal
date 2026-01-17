package com.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
import com.payments.service.helper.PPCaptureOrderHelper;
import com.payments.service.helper.PPCreateOrderHelper;
import com.payments.services.PaymentStatusService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTestAi {

    @Mock
    private PPCreateOrderHelper ppCreateOrderHelper;

    @Mock
    private PPCaptureOrderHelper ppCaptureOrderHelper;

    @Mock
    private HttpServiceEngine httpServiceEngine;

    @Mock
    private PaymentStatusService paymentStatusService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    /* -------------------- createPayment() -------------------- */

    @Test
    void testCreatePayment_success() {
        CreatePaymentRequest request = new CreatePaymentRequest();

        TransactionDto mappedDto = new TransactionDto();
        TransactionDto processedDto = new TransactionDto();
        processedDto.setTxnReference("txn-123");
        processedDto.setTxnStatusId(1);

        when(modelMapper.map(request, TransactionDto.class))
                .thenReturn(mappedDto);

        when(paymentStatusService.processPayment(any(TransactionDto.class)))
                .thenReturn(processedDto);

        PaymentResponse txnResponse =
                paymentServiceImpl.createPayment(request);

        assertNotNull(txnResponse);
        assertNotNull(txnResponse.getTxnReference());
        assertNotNull(txnResponse.getTxnStatusId());
        assertEquals(1, txnResponse.getTxnStatusId());

        verify(modelMapper).map(request, TransactionDto.class);
        verify(paymentStatusService).processPayment(any(TransactionDto.class));
    }

    /* -------------------- initiatePayment() SUCCESS -------------------- */

    @Test
    void testInitiatePayment_success() {
        String txnReference = "txn-123";
        InitiatePaymentRequest initiateRequest = new InitiatePaymentRequest();

        Transaction txnEntity = new Transaction();
        TransactionDto txnDto = new TransactionDto();
        txnDto.setTxnReference(txnReference);

        HttpRequest httpRequest = new HttpRequest();

        PPOrderResponse ppResponse = new PPOrderResponse();
        ppResponse.setOrderId("PP-ORDER-1");
        ppResponse.setRedirectUrl("https://paypal.com/redirect");

        when(transactionDao.getTransactionByTxnReference(txnReference))
                .thenReturn(txnEntity);

        when(modelMapper.map(txnEntity, TransactionDto.class))
                .thenReturn(txnDto);

        when(paymentStatusService.processPayment(any(TransactionDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(ppCreateOrderHelper.prepareHttpRequest(
                eq(txnReference), eq(initiateRequest), any(TransactionDto.class)))
                .thenReturn(httpRequest);

        when(httpServiceEngine.makeHttpCall(httpRequest))
                .thenReturn(ResponseEntity.ok("success"));

        when(ppCreateOrderHelper.processResponse(any(ResponseEntity.class)))
                .thenReturn(ppResponse);

        PaymentResponse response =
                paymentServiceImpl.initiatePayment(txnReference, initiateRequest);

        assertNotNull(response);
        assertEquals(3, response.getTxnStatusId()); // PENDING
        assertEquals("PP-ORDER-1", response.getPaypalProviderReference());
        assertEquals("https://paypal.com/redirect", response.getRedirectUrl());

        verify(transactionDao).getTransactionByTxnReference(txnReference);
        verify(ppCreateOrderHelper).processResponse(any(ResponseEntity.class));
    }

    /* -------------------- initiatePayment() FAILURE -------------------- */

    @Test
    void testInitiatePayment_paypalFailure() {
        String txnReference = "txn-123";
        InitiatePaymentRequest initiateRequest = new InitiatePaymentRequest();

        Transaction txnEntity = new Transaction();
        TransactionDto txnDto = new TransactionDto();
        txnDto.setTxnReference(txnReference);

        HttpRequest httpRequest = new HttpRequest();

        ProcessingServiceException exception =
                new ProcessingServiceException("ERR_500",
                		"PayPal failure", HttpStatus.INTERNAL_SERVER_ERROR);

        when(transactionDao.getTransactionByTxnReference(txnReference))
                .thenReturn(txnEntity);

        when(modelMapper.map(txnEntity, TransactionDto.class))
                .thenReturn(txnDto);

        when(paymentStatusService.processPayment(any(TransactionDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(ppCreateOrderHelper.prepareHttpRequest(
                eq(txnReference), eq(initiateRequest), any(TransactionDto.class)))
                .thenReturn(httpRequest);

        when(httpServiceEngine.makeHttpCall(httpRequest))
                .thenThrow(exception);

        ProcessingServiceException thrown =
                assertThrows(ProcessingServiceException.class, () ->
                        paymentServiceImpl.initiatePayment(txnReference, initiateRequest));

        assertEquals("ERR_500", thrown.getErrorCode());

        verify(paymentStatusService, atLeastOnce())
                .processPayment(any(TransactionDto.class)); // FAILED status updated
    }

    /* -------------------- capturePayment() -------------------- */

    @Test
    void testCapturePayment_success() {
        String txnReference = "txn-123";

        Transaction txnEntity = new Transaction();
        TransactionDto txnDto = new TransactionDto();
        txnDto.setTxnReference(txnReference);

        HttpRequest httpRequest = new HttpRequest();
        PPOrderResponse ppResponse = new PPOrderResponse();

        when(transactionDao.getTransactionByTxnReference(txnReference))
                .thenReturn(txnEntity);

        when(modelMapper.map(txnEntity, TransactionDto.class))
                .thenReturn(txnDto);

        when(paymentStatusService.processPayment(any(TransactionDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(ppCaptureOrderHelper.prepareHttpRequest(eq(txnReference),
        		any(TransactionDto.class)))
                .thenReturn(httpRequest);

        when(httpServiceEngine.makeHttpCall(httpRequest))
                .thenReturn(ResponseEntity.ok("success"));

        when(ppCaptureOrderHelper.processResponse(any(ResponseEntity.class)))
                .thenReturn(ppResponse);

        PaymentResponse response =
                paymentServiceImpl.capturePayment(txnReference);

        assertNotNull(response);
        assertEquals(5, response.getTxnStatusId()); // SUCCESS
        assertEquals(txnReference, response.getTxnReference());

        verify(ppCaptureOrderHelper).processResponse(any(ResponseEntity.class));
    }
}
