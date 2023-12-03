package org.home.weatherservice.mapper;

import lombok.extern.slf4j.Slf4j;
import org.home.weatherservice.dto.AverageWeatherDataDto;
import org.home.weatherservice.dto.api.response.AggregatedWeatherApiResponseDto;
import org.home.weatherservice.dto.api.response.WeatherApiResponseDto;
import org.home.weatherservice.dto.weather.response.WeatherDto;
import org.home.weatherservice.entity.Weather;
import org.home.weatherservice.exception.InconsistentWeatherDataException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.function.Function;

import static java.math.RoundingMode.HALF_UP;

@Service
@Slf4j
public class WeatherMapper {
    public static final int DEFAULT_BIG_DECIMAL_SCALE = 2;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = HALF_UP;

    private Function<Double, BigDecimal> toBigDecimal = doubleVal ->
            BigDecimal.valueOf(Objects.requireNonNullElse(doubleVal, 0D))
                    .setScale(DEFAULT_BIG_DECIMAL_SCALE, DEFAULT_ROUNDING_MODE);

    public Weather mapToEntity(WeatherDto dto) {
        log.debug("Мапим ответ от клиента: {}", dto);
        var location = dto.location();
        var dataDto = dto.current();

        if (location == null || dataDto == null) {
            throw new InconsistentWeatherDataException(location, dataDto);
        }

        var entity = new Weather()
                .setLocation(location.name())
                .setTemperature(dataDto.tempC())
                .setWindSpeed(dataDto.windMph())
                .setAtmospherePressure(dataDto.pressureMb())
                .setHumidity(dataDto.humidity())
                .setWeatherConditions(dataDto.condition().text())
                .setLastUpdateEpoch(LocalDateTime.ofEpochSecond(dataDto.lastUpdatedEpoch(), 0, ZoneOffset.UTC));

        log.debug("Полученная сущность после маппинга: {}", dto);
        return entity;
    }

    public WeatherApiResponseDto toApiResponseDto(Weather entity) {
        return new WeatherApiResponseDto(
                entity.getTemperature(),
                entity.getWindSpeed(),
                entity.getAtmospherePressure(),
                entity.getHumidity(),
                entity.getWeatherConditions(),
                entity.getLocation()
        );
    }

    public AggregatedWeatherApiResponseDto toApiAggregatedResponseDto(AverageWeatherDataDto entity) {
        return new AggregatedWeatherApiResponseDto(
                toBigDecimal.apply(entity.getTemperature()),
                toBigDecimal.apply(entity.getWindSpeed()),
                toBigDecimal.apply(entity.getAtmospherePressureMBar()),
                toBigDecimal.apply(entity.getHumidity())
        );
    }
}
