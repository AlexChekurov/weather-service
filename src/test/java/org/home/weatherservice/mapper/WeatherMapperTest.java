package org.home.weatherservice.mapper;

import org.home.weatherservice.dto.AverageWeatherDataDto;
import org.home.weatherservice.dto.api.response.AggregatedWeatherApiResponseDto;
import org.home.weatherservice.dto.api.response.WeatherApiResponseDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.home.weatherservice.DataFactory.buildWeatherDto;
import static org.home.weatherservice.DataFactory.buildWeatherEntity;

class WeatherMapperTest {

    private static final WeatherMapper MAPPER = new WeatherMapper();


    @Test
    void mapToEntityTest() {
        var weatherDto = buildWeatherDto();
        var expectedEntity = buildWeatherEntity(weatherDto);

        var actualEntity = MAPPER.mapToEntity(weatherDto);

        assertThat(actualEntity).usingRecursiveComparison().isEqualTo(expectedEntity);
    }

    @Test
    void toApiResponseDtoTest() {
        var entity = buildWeatherEntity(buildWeatherDto());
        var expectedDto = new WeatherApiResponseDto(
                entity.getTemperature(),
                entity.getWindSpeed(),
                entity.getAtmospherePressure(),
                entity.getHumidity(),
                entity.getWeatherConditions(),
                entity.getLocation()
        );

        var actualDto = MAPPER.toApiResponseDto(entity);

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void toApiAggregatedResponseDtoTest() {
        var aggregatedDto = new AverageWeatherDataDto(12.431111D, 43.3333D, 12D, 34.666666666);
        var expectedDto = new AggregatedWeatherApiResponseDto(
                new BigDecimal(12.43).setScale(2, HALF_UP),
                new BigDecimal(43.33).setScale(2, HALF_UP),
                new BigDecimal(12.00).setScale(2, HALF_UP),
                new BigDecimal(34.67).setScale(2, HALF_UP)
        );

        var actualDto = MAPPER.toApiAggregatedResponseDto(aggregatedDto);

        assertThat(actualDto).usingRecursiveAssertion().isEqualTo(expectedDto);
    }

    @Test
    void toEmptyApiAggregatedResponseDtoTest() {
        var aggregatedDto = new AverageWeatherDataDto(null, null, null, null);
        var expectedDto = new AggregatedWeatherApiResponseDto(
                BigDecimal.ZERO.setScale(2, HALF_UP),
                BigDecimal.ZERO.setScale(2, HALF_UP),
                BigDecimal.ZERO.setScale(2, HALF_UP),
                BigDecimal.ZERO.setScale(2, HALF_UP)
        );

        var actualDto = MAPPER.toApiAggregatedResponseDto(aggregatedDto);

        assertThat(actualDto).usingRecursiveAssertion().isEqualTo(expectedDto);
    }
}