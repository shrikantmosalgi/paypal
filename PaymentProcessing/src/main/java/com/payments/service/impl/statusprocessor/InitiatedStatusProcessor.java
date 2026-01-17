package com.payments.service.impl.statusprocessor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payments.dao.TransactionDao;
import com.payments.dto.TransactionDto;
import com.payments.entity.Transaction;
import com.payments.service.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InitiatedStatusProcessor implements TransactionStatusProcessor {

	@Autowired
	private TransactionDao transactionDao;
	@Autowired
	private  ModelMapper modelMapper;
	
	@Override
	public TransactionDto processStatus(TransactionDto transactionDto) {
		log.info("Processing Initiated Status for transactionDto: {} ", transactionDto);
		
		Transaction transaction =modelMapper.map(transactionDto, Transaction.class);
		
		transactionDao.updateTransaction(transaction);
		
		return transactionDto;
	}

}
