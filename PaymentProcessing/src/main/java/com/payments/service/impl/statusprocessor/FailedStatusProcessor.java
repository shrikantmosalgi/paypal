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
public class FailedStatusProcessor implements TransactionStatusProcessor {

	@Autowired
	private TransactionDao transactionDao;
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("Processing 'FAILED' status for txnDto: {}", txnDto);

		// convert DTO to Entity
		Transaction txnEntity = modelMapper.map(
				txnDto, Transaction.class);

		transactionDao.updateTransaction(txnEntity);
		log.info("Updated TransactionEntity in DB for FAILED status: {}", txnEntity);

		return txnDto;
	}

}
