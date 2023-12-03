package org.home.weatherservice.dto.weather.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConditionDto(String text, Integer code) {
}
