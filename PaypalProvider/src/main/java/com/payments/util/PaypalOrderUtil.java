package com.payments.util;

import com.payments.response.error.PaypalErrorDetail;
import com.payments.response.error.PaypalErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaypalOrderUtil {

	private PaypalOrderUtil() {

	}

	public static String getPaypalErrorSummary(PaypalErrorResponse paypalErrorResponse) {

		if (paypalErrorResponse == null) {
			return "unknown paypal error";
		}

		StringBuilder errorSummary = new StringBuilder();

		appendIfPresent(errorSummary, paypalErrorResponse.getName());
		appendIfPresent(errorSummary, paypalErrorResponse.getMessage());
		appendIfPresent(errorSummary, paypalErrorResponse.getError());
		appendIfPresent(errorSummary, paypalErrorResponse.getErrorDescription());

		if (paypalErrorResponse.getDetails() != null && !paypalErrorResponse.getDetails().isEmpty()) {
			PaypalErrorDetail detail = paypalErrorResponse.getDetails().get(0);

			if (detail != null) {
				appendIfPresent(errorSummary, detail.getIssue());
				appendIfPresent(errorSummary, detail.getField());
				appendIfPresent(errorSummary, detail.getDescription());

			}
		}

		return errorSummary.length() > 0 ? errorSummary.toString() : "unknown paypal error";
	}

	private static void appendIfPresent(StringBuilder sb, String value) {
		if (value != null && !value.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(" | ");
			}
			sb.append(value.trim());
		}
	}
}
