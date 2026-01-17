package com.payments.service;

import com.payments.dto.TransactionDto;

public interface TransactionStatusProcessor {

	public TransactionDto processStatus(TransactionDto transactionDto);
}
