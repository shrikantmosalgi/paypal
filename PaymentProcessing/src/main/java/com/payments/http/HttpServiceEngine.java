package com.payments.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.payments.constant.ErrorCodeEnum;
import com.payments.exception.ProcessingServiceException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpServiceEngine {// this class for making external http calls

	@Autowired
	RestClient restClient; 
	/* For calling external api we use RestClient
	 * we have configured this manually in AppConfig class because spring is not managing it 
	 * */

	@CircuitBreaker(name = "payment-processing-service", fallbackMethod = "fallbackProcessPayment")
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("making http call from HttpServiceEngine");
		try {
			// prepare api call
			ResponseEntity<String> httpResponse = restClient.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl().trim())
					.headers(restClientHeaders -> restClientHeaders.addAll(httpRequest.getHttpHeaders()))
					.body(httpRequest.getBody())
					.retrieve()
					.toEntity(String.class);

			log.info("http call completed httpResponse: {}", httpResponse);
			return httpResponse;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("HTTP error during http call: {}", e);
			
			if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
					|| e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				log.error("Service unavailable or gateway timeout ",e);
				throw new ProcessingServiceException(ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorCode(),
						ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorMessage(),
						HttpStatus.SERVICE_UNAVAILABLE);
			}			
			return ResponseEntity
					.status(e.getStatusCode())
					.body(e.getResponseBodyAsString());			
		}catch (Exception e) {
			log.error("error during http call: {}", e);
			throw new ProcessingServiceException(ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorCode(),
					ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorMessage(),
					HttpStatus.SERVICE_UNAVAILABLE);
		}

	}
	
	public ResponseEntity<String> fallbackProcessPayment(HttpRequest httpRequest, Throwable t) {
		// Handle fallback logic here
		log.error("Fallback method called due to: {}", t.getMessage(), t);
		throw new ProcessingServiceException(
				ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorCode(),
				ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}


}
