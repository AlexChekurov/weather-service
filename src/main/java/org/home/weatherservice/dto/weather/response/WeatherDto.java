package org.home.weatherservice.dto.weather.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDto(LocationDto location, DataDto current) {
}
