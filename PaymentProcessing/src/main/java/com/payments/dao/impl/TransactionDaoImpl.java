package com.payments.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.payments.constant.ErrorCodeEnum;
import com.payments.dao.TransactionDao;
import com.payments.entity.Transaction;
import com.payments.exception.ProcessingServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TransactionDaoImpl implements TransactionDao {
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Transaction createTransaction(Transaction transaction) {

		log.info("Creating transaction: {}", transaction);
		String sql = "INSERT INTO Transaction (" + "userId, paymentMethodId,"
				+ " providerId, paymentTypeId, txnStatusId, "
				+ "amount, currency, merchantTransactionReference, txnReference, "
				+ "providerReference, errorCode, errorMessage, retryCount" + ") VALUES ("
				+ ":userId, :paymentMethodId, :providerId, :paymentTypeId, :txnStatusId, "
				+ ":amount, :currency, :merchantTransactionReference, :txnReference, "
				+ ":providerReference, :errorCode, :errorMessage, :retryCount" + ")";

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("userId", transaction.getUserId())
				.addValue("paymentMethodId", transaction.getPaymentMethodId())
				.addValue("providerId", transaction.getProviderId())
				.addValue("paymentTypeId", transaction.getPaymentTypeId())
				.addValue("txnStatusId", transaction.getTxnStatusId())
				.addValue("amount", transaction.getAmount())
				.addValue("currency", transaction.getCurrency())
				.addValue("merchantTransactionReference", transaction.getMerchantTransactionReference())
				.addValue("txnReference", transaction.getTxnReference())
				.addValue("providerReference", transaction.getProviderReference())
				.addValue("errorCode", transaction.getErrorCode())
				.addValue("errorMessage", transaction.getErrorMessage())
				.addValue("retryCount", transaction.getRetryCount());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// if we dont want to return transaction then keyHolder, new String[]{"id"} not
		// required
		jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });

		// Read auto-generated DB ID
		Number generatedId = keyHolder.getKey();
		if (generatedId != null) {
			transaction.setId(generatedId.intValue());
		}

		log.info("Created transaction with ID: {}", transaction.getId());
		return transaction;
	}

	@Override
	public Transaction getTransactionByTxnReference(String txnReference) {

		String sql = "SELECT * FROM Transaction WHERE txnReference = :txnReference";

		Map<String, Object> params = new HashMap<>();
		params.put("txnReference", txnReference);

		Transaction transaction = jdbcTemplate.queryForObject(sql, params,
				new BeanPropertyRowMapper<>(Transaction.class));
		log.info("Fetched transaction: {}", transaction);
		return transaction;

	}

	@Override
	public void updateTransaction(Transaction transaction) {

		String sql = "UPDATE `Transaction` " + "SET txnStatusId = :txnStatusId, "
				+ "    providerReference = :providerReference, " + "    errorCode = :errorCode, "
				+ "    errorMessage = :errorMessage " + "WHERE id = :id";
				

		Map<String, Object> params = new HashMap<>();
		params.put("txnStatusId", transaction.getTxnStatusId());
		params.put("providerReference", transaction.getProviderReference());
		params.put("errorCode", transaction.getErrorCode());
		params.put("errorMessage", transaction.getErrorMessage());
		params.put("id", transaction.getId());

		int rowsAffected = jdbcTemplate.update(sql, params);

		if (rowsAffected == 0) {
			log.error("No transaction found to update with ID: {}", transaction.getId());
			throw new ProcessingServiceException(ErrorCodeEnum.ERROR_UPDATING_TRANSACTION.getErrorCode(),
					ErrorCodeEnum.ERROR_UPDATING_TRANSACTION.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		log.info("Updated transaction ID: {} | Rows affected: {}", transaction.getId(), rowsAffected);

	}

}
