package com.payments.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.payments.constant.ErrorCodeEnum;
import com.payments.dto.TransactionDto;
import com.payments.exception.ProcessingServiceException;
import com.payments.service.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentStatusService {

	
	@Autowired
	private PaymentStatusFactory paymentStatusFactory;
	
	public TransactionDto processPayment(TransactionDto transactionDto) {
		
		int statusId = transactionDto.getTxnStatusId();
		TransactionStatusProcessor processor =paymentStatusFactory.getStatusProcessor(statusId);

		if (processor == null) {
			log.error("No TransactionStatusProcessor found for statusId: {}", statusId);
			throw new ProcessingServiceException(ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorCode(),
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		 transactionDto =processor.processStatus(transactionDto);
		log.info("Processed payment status with response: {}", transactionDto);
		return transactionDto;
    }
}
