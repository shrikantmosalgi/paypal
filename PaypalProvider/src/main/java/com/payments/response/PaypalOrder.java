package com.payments.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaypalOrder {
    private String id;
    private String status;
    @JsonProperty("payment_source")
    private PaymentSource payment_source;
    private List<PaypalLink> links;

}

