package com.payments.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.payments.service.TransactionStatusProcessor;
import com.payments.service.impl.statusprocessor.ApprovedStatusProcessor;
import com.payments.service.impl.statusprocessor.CreatedStatusProcessor;
import com.payments.service.impl.statusprocessor.FailedStatusProcessor;
import com.payments.service.impl.statusprocessor.InitiatedStatusProcessor;
import com.payments.service.impl.statusprocessor.PendingStatusProcessor;
import com.payments.service.impl.statusprocessor.SuccessStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentStatusFactory {
	
	@Autowired
	private CreatedStatusProcessor createdStatusProcessor;
	@Autowired
	private InitiatedStatusProcessor initiatedStatusProcessor;
	@Autowired
	private ApprovedStatusProcessor approvedStatusProcessor;
	@Autowired
	private FailedStatusProcessor failedStatusProcessor;
	@Autowired
	private PendingStatusProcessor pendingStatusProcessor;
	@Autowired
	private SuccessStatusProcessor successStatusProcessor;
	
	
	public TransactionStatusProcessor getStatusProcessor(int statusId) {
		log.info("Fetching TransactionStatusProcessor for status: {}", statusId);
		
		switch (statusId) {
		case 1:
			log.info("Returning CreatedStatusProcessor for statusId: {}", statusId);
			 return  createdStatusProcessor;
			 
		case 2:
			log.info("Returning InitiatedStatusProcessor for statusId: {}", statusId);
			return initiatedStatusProcessor;
		case 3:
			log.info("Returning PendingStatusProcessor for statusId: {}", statusId);
			return pendingStatusProcessor;
		case 4:
			log.info("Returning ApprovedStatusProcessor for statusId: {}", statusId);
			return approvedStatusProcessor;
			
		case 5:
			log.info("Returning SuccessStatusProcessor for statusId: {}", statusId);
			return successStatusProcessor;
			
		case 6:
			log.info("Returning FailedStatusProcessor for statusId: {}", statusId);
			return failedStatusProcessor;	
		default:
			log.warn("No processor found for statusId: {}", statusId);
			return null;
		}
		
		
	}

}
