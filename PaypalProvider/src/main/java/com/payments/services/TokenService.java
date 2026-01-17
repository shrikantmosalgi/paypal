package com.payments.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.payments.constant.Constant;
import com.payments.http.HttpRequest;
import com.payments.http.HttpServiceEngine;
import com.payments.response.PaypalOAuthToken;
import com.payments.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenService {

	
	@Autowired
	private HttpServiceEngine httpServiceEngine;
	@Autowired
	private JsonUtil jsonUtil;
	@Autowired
	private RedisService redisService;

	

	@Value("${paypal.client.id}")
	private String clientId;
	@Value("${paypal.client.secret}")
	private String clientSecret;
	@Value("${paypal.oauth.url}")
	private String oauthUrl;

	public String getAccessToken() {
		log.info("getting access token from TokenService");

		/*
		 * paypal support OAuth2.0 security so we have to implement it to call paypal
		 * API
		 */
		
		String accessToken =redisService.getValue(Constant.PAYPAL_ACCESS_TOKEN);
		
		if (accessToken != null) {
			return accessToken;
		}
		// for headers
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(clientId, clientSecret);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// body
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(oauthUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(formData);
		// call paypal OAuth server to get access token
		ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);

		String tokenBody = response.getBody();
		log.info("access token got from TokenService");

		PaypalOAuthToken token = jsonUtil.convertJsonToObject(tokenBody, PaypalOAuthToken.class);
		accessToken = token.getAccessToken();
		
		// Cache the access token in Redis with expiry time
				redisService.setValueWithExpiry(
						Constant.PAYPAL_ACCESS_TOKEN, 
						accessToken, 
						token.getExpiresIn() - Constant.REDIS_ACCESS_TOKEN_EXPIRY_DIFF); // subtracting 60 seconds as buffer

		return token.getAccessToken();

	}
}
