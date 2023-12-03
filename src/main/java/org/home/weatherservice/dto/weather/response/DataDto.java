package org.home.weatherservice.dto.weather.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataDto(
        Integer lastUpdatedEpoch,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime localtime,
        Float tempC,
        Float tempF,
        Integer isDay,
        ConditionDto condition,
        Float windMph,
        Float windKph,
        Float windDegree,
        String windDir,
        Float pressureMb,
        Float pressureIn,
        Short humidity,
        Integer cloud,
        Float feelslikeC,
        Float feelslikeF,
        Float visKm,
        Float visMiles
) {
}
