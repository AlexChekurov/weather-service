package org.home.weatherservice.dto.weather.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationDto(String name,
                          String region,
                          String country,
                          Float lat,
                          Float lon,
                          String tzId,
                          Long localtimeEpoch,
                          @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
                          LocalDateTime localtime) {
}