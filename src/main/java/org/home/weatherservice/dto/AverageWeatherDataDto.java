package org.home.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AverageWeatherDataDto {
    private Double temperature;
    private Double windSpeed;
    private Double atmospherePressureMBar;
    private Double humidity;
}
