package com.payments.dao;

import com.payments.entity.Transaction;

public interface TransactionDao {

	public Transaction createTransaction(Transaction transaction);
	public Transaction getTransactionByTxnReference(String txnReference);
	public void updateTransaction(Transaction transaction);
}

