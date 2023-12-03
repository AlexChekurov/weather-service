package org.home.weatherservice.dto.api.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedWeatherApiResponseDto {
    private BigDecimal avgTemperature;
    private BigDecimal avgWindSpeed;
    private BigDecimal avgAtmospherePressureMBar;
    private BigDecimal avgHumidity;
}
