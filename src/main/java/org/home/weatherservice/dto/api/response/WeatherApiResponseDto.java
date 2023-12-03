package org.home.weatherservice.dto.api.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WeatherApiResponseDto {
    private Float temperature;
    private Float windSpeed;
    private Float atmospherePressureMBar;
    private Short humidity;
    private String weatherConditions;
    private String location;
}
