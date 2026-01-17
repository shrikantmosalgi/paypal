package com.payments.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Paypal {

	@JsonProperty("experience_context")
    private ExperienceContext experienceContext;
}
