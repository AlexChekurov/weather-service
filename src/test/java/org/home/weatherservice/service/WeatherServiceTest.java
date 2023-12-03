package org.home.weatherservice.service;

import org.home.weatherservice.clients.WeatherApiFeignClient;
import org.home.weatherservice.dto.AverageWeatherDataDto;
import org.home.weatherservice.dto.api.response.AggregatedWeatherApiResponseDto;
import org.home.weatherservice.dto.api.response.WeatherApiResponseDto;
import org.home.weatherservice.exception.NoDataForLocationException;
import org.home.weatherservice.mapper.WeatherMapper;
import org.home.weatherservice.repository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.home.weatherservice.DataFactory.buildWeatherDto;
import static org.home.weatherservice.DataFactory.buildWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherApiFeignClient weatherFeignClient;

    @Mock
    private WeatherRepository weatherRepository;

    @Spy
    private WeatherMapper weatherMapper = new WeatherMapper();

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getWeatherFromApiTest() {
        ReflectionTestUtils.setField(weatherService, "defaultLocation", "Metropolis");
        var dto = buildWeatherDto();
        when(weatherFeignClient.getCurrentWeather("Metropolis")).thenReturn(dto);

        var actualDto = weatherService.getWeatherFromApi();

        assertThat(actualDto).usingRecursiveAssertion().isEqualTo(dto);

    }

    @Test
    void getWeatherWithErrorFromApiTest() {
        when(weatherFeignClient.getCurrentWeather(any())).thenThrow(NullPointerException.class);

        var actualDto = weatherService.getWeatherFromApi();

        assertNull(actualDto);

    }

    @Test
    void findLastWeatherDataTest() {
        var location = "     Gotham city    ";
        var entity = buildWeatherEntity(buildWeatherDto());
        when(weatherRepository.findFirstByLocationOrderByLastUpdateEpochDesc("Gotham city"))
                .thenReturn(Optional.ofNullable(entity));
        var expectedDto = new WeatherApiResponseDto(
                entity.getTemperature(),
                entity.getWindSpeed(),
                entity.getAtmospherePressure(),
                entity.getHumidity(),
                entity.getWeatherConditions(),
                entity.getLocation()
        );

        var actualDto = weatherService.findLastWeatherData(location);

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void findLastWeatherNoDataTest() {
        var location = "     Gotham city    ";
        when(weatherRepository.findFirstByLocationOrderByLastUpdateEpochDesc("Gotham city"))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                NoDataForLocationException.class,
                () -> weatherService.findLastWeatherData(location)
        );

        assertThat(exception.getMessage()).contains("Gotham city");
    }

    @Test
    void findAggregatedWeatherDataTest() {
        ReflectionTestUtils.setField(weatherService, "defaultLocation", "Wakanda");
        var from = LocalDate.parse("2023-12-01");
        var to = LocalDate.parse("2023-12-01");
        var aggregatedDto = new AverageWeatherDataDto(12.431111D, 43.3333D, 12D, 34.666666666);
        var expectedDto = new AggregatedWeatherApiResponseDto(
                new BigDecimal(12.43).setScale(2, HALF_UP),
                new BigDecimal(43.33).setScale(2, HALF_UP),
                new BigDecimal(12.00).setScale(2, HALF_UP),
                new BigDecimal(34.67).setScale(2, HALF_UP)
        );
        when(weatherRepository.findAvgWeatherByLocationAndPeriod(
                "Wakanda",
                LocalDateTime.parse("2023-12-01T00:00"),
                LocalDateTime.parse("2023-12-01T00:00")))
                .thenReturn(Optional.of(aggregatedDto));

        var actualDto = weatherService.findAggregatedWeatherData(null, from, to);

        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);

    }

    @Test
    void findEmptyAggregatedWeatherDataTest() {
        ReflectionTestUtils.setField(weatherService, "defaultLocation", "Mumbai");
        var from = LocalDate.parse("2023-12-02");
        var to = LocalDate.parse("2023-12-03");
        var aggregatedDto = new AverageWeatherDataDto(null, null, null, null);

        when(weatherRepository.findAvgWeatherByLocationAndPeriod(
                "Mumbai",
                LocalDateTime.parse("2023-12-02T00:00"),
                LocalDateTime.parse("2023-12-03T00:00")))
                .thenReturn(Optional.of(aggregatedDto));

        var exception = assertThrows(
                NoDataForLocationException.class,
                () -> weatherService.findAggregatedWeatherData(null, from, to)
        );

        assertThat(exception.getMessage()).contains("Mumbai");

    }

    @Test
    void findNoAggregatedWeatherDataTest() {
        when(weatherRepository.findAvgWeatherByLocationAndPeriod(any(), any(), any()))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                NoDataForLocationException.class,
                () -> weatherService.findAggregatedWeatherData("Jakarta", LocalDate.now(), LocalDate.now())
        );

        assertThat(exception.getMessage()).contains("Нет данных о погоде для Jakarta");

    }

    @Test
    void updateWeatherTest() {
        var location = "Metropolis";
        ReflectionTestUtils.setField(weatherService, "defaultLocation", location);
        var dto = buildWeatherDto();
        when(weatherFeignClient.getCurrentWeather(location)).thenReturn(dto);

        weatherService.updateWeather();

        verify(weatherRepository)
                .existsByLocationAndLastUpdateEpoch(dto.location().name(), LocalDateTime.parse("1970-01-01T03:23:54"));
        verify(weatherRepository).save(any());
        verifyNoMoreInteractions(weatherRepository);
    }

    @Test
    void updateWeatherEntityExistsTest() {
        var dto = buildWeatherDto();
        when(weatherFeignClient.getCurrentWeather(any())).thenReturn(dto);
        when(weatherRepository.existsByLocationAndLastUpdateEpoch(
                dto.location().name(),
                LocalDateTime.parse("1970-01-01T03:23:54")))
                .thenReturn(true);

        weatherService.updateWeather();

        verifyNoMoreInteractions(weatherRepository);
    }

    @Test
    void updateWeatherEntityWithClientExceptionTest() {
        when(weatherFeignClient.getCurrentWeather(any())).thenThrow(NullPointerException.class);

        weatherService.updateWeather();

        verifyNoInteractions(weatherMapper);
        verifyNoInteractions(weatherRepository);
    }

    @Test
    void updateWeatherEntityWithMapperExceptionTest() {
        var dto = buildWeatherDto();
        when(weatherFeignClient.getCurrentWeather(any())).thenReturn(dto);
        when(weatherRepository.existsByLocationAndLastUpdateEpoch(any(), any()))
                .thenThrow(ArithmeticException.class);

        weatherService.updateWeather();

        verify(weatherRepository).existsByLocationAndLastUpdateEpoch(any(), any());
        verifyNoMoreInteractions(weatherRepository);
    }
}