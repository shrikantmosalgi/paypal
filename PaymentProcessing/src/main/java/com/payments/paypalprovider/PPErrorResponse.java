package com.payments.paypalprovider;

import lombok.Data;

@Data
public class PPErrorResponse {

	private String errorCode;
	private String errorMessage;
}
