package com.payments.config;

import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

//	@Bean
//	RestClient restClient() {		
//		return RestClient.create();		
//	}
	
	@Bean
    public RestClient restClient(RestClient.Builder builder) {
        // Create connection pool manager
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);           // total max connections
        connectionManager.setDefaultMaxPerRoute(100);  // max per host

        // Create the HttpClient using the pool
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(TimeValue.ofSeconds(30)) // cleanup idle connections
                .build();

        // Wrap HttpClient in a Spring factory
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectionRequestTimeout(10000);
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(15000);
        // Build RestClient using factory
        return builder   //build restClient
                .requestFactory(requestFactory)
                .build();
    }

}
