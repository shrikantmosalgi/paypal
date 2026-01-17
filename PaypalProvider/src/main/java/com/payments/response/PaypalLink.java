package com.payments.response;

import lombok.Data;

@Data
public class PaypalLink {
	
	private String href;
    private String rel;
    private String method;
}
