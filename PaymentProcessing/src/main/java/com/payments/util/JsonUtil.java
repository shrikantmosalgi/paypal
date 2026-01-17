package com.payments.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JsonUtil {
	
	@Autowired
	private ObjectMapper objectMapper;

	public <T> T convertJsonToObject(String jsonString, Class<T> clazz) {
		try {
			return objectMapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			log.error("Error converting JSON to Object: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public String convertObjectToJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			log.error("Error converting Object to JSON: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
}
