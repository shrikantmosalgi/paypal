package com.payments.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Transaction {

    private int id;
    private int userId;

    private int paymentMethodId;
    private int providerId;
    private int paymentTypeId;
    private int txnStatusId;

    private BigDecimal amount;
    private String currency;

    private String merchantTransactionReference;
    private String txnReference;
    private String providerReference;

    private String errorCode;
    private String errorMessage;

    private Timestamp creationDate;
    private int retryCount;
}
