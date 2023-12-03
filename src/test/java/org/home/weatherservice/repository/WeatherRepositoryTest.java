package org.home.weatherservice.repository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.home.weatherservice.AbstractSpringBootPostgresTest;
import org.home.weatherservice.dto.AverageWeatherDataDto;
import org.home.weatherservice.entity.Weather;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WeatherRepositoryTest extends AbstractSpringBootPostgresTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @AfterEach
    private void clear() {
        weatherRepository.deleteAll();
    }

    @Test
    void saveToDataBaseTest() {
        var weatherRecord = new Weather()
                .setTemperature(25F)
                .setWindSpeed(12F)
                .setAtmospherePressure(1000.2344F)
                .setHumidity((short) 50)
                .setWeatherConditions("Mist")
                .setLocation("London")
                .setLastUpdateEpoch(LocalDateTime.now());
        weatherRepository.saveAndFlush(weatherRecord);

        var actualRecords = weatherRepository.findAll();
        assertThat(actualRecords).hasSize(1);
        assertThat(actualRecords.get(0)).usingRecursiveComparison().isEqualTo(weatherRecord);
    }

    @Test
    void errorOnSaveBadEntityTest() {
        var expectedViolatedFields = Set.of("windSpeed", "temperature", "humidity",
                "atmospherePressure", "lastUpdateEpoch", "location", "weatherConditions");
        var weatherRecord = new Weather();

        var exception = assertThrows(
                ConstraintViolationException.class,
                () -> weatherRepository.saveAndFlush(weatherRecord)
        );

        assertNotNull(exception);
        assertThat(exception.getConstraintViolations()).hasSize(7);
        var badFieldsSet = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .collect(Collectors.toSet());
        assertThat(badFieldsSet).containsExactlyInAnyOrderElementsOf(expectedViolatedFields);
    }

    @Test
    void successExistsTest() {
        var weatherRecord = new Weather()
                .setTemperature(32F)
                .setWindSpeed(1F)
                .setAtmospherePressure(1005.2344F)
                .setHumidity((short) 49)
                .setWeatherConditions("Mist")
                .setLocation("Oslo")
                .setLastUpdateEpoch(LocalDateTime.now());
        weatherRepository.saveAndFlush(weatherRecord);

        boolean expectExists = weatherRepository.existsByLocationAndLastUpdateEpoch(
                "Oslo",
                weatherRecord.getLastUpdateEpoch());
        boolean expectNotExists = weatherRepository.existsByLocationAndLastUpdateEpoch(
                "London",
                weatherRecord.getLastUpdateEpoch());
        assertTrue(expectExists);
        assertFalse(expectNotExists);
    }

    @Test
    void successFindLastTest() {
        var expectedWeather = new Weather()
                .setTemperature(11F)
                .setWindSpeed(3F)
                .setAtmospherePressure(1012.2344F)
                .setHumidity((short) 99)
                .setWeatherConditions("Sunny")
                .setLocation("London")
                .setLastUpdateEpoch(LocalDateTime.now().plusDays(1));
        var recordsList = buildWeatherList();
        recordsList.add(expectedWeather);
        weatherRepository.saveAllAndFlush(recordsList);

        var actualRecord = weatherRepository.findFirstByLocationOrderByLastUpdateEpochDesc("London");

        assertThat(actualRecord)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedWeather);
    }

    @Test
    void successCalculateAverageWeatherTest() {
        var anotherWeather = new Weather()
                .setTemperature(3F)
                .setWindSpeed(2F)
                .setAtmospherePressure(1034F)
                .setHumidity((short) 45)
                .setWeatherConditions("Sunny")
                .setLocation("London")
                .setLastUpdateEpoch(LocalDateTime.now());
        var ignoredWeather = new Weather()
                .setTemperature(10000F)
                .setWindSpeed(200000F)
                .setAtmospherePressure(1034000.2344F)
                .setHumidity((short) 200)
                .setWeatherConditions("Sunny")
                .setLocation("London")
                .setLastUpdateEpoch(LocalDateTime.now().plusDays(12));
        var recordsList = buildWeatherList();
        recordsList.add(anotherWeather);
        recordsList.add(ignoredWeather);
        weatherRepository.saveAllAndFlush(recordsList);
        var expectedWeather = new AverageWeatherDataDto(20.0, 5.0, 1013.15625, 48.0);

        var averageWeatherDataDto = weatherRepository.findAvgWeatherByLocationAndPeriod("London",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(3));

        assertThat(averageWeatherDataDto)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedWeather);
    }

    private List<Weather> buildWeatherList() {
        var result = new ArrayList<Weather>();
        var weatherRecordOne = new Weather()
                .setTemperature(32F)
                .setWindSpeed(1F)
                .setAtmospherePressure(1005.2344F)
                .setHumidity((short) 49)
                .setWeatherConditions("Mist")
                .setLocation("London")
                .setLastUpdateEpoch(LocalDateTime.now());
        var weatherRecordTwo = new Weather()
                .setTemperature(25F)
                .setWindSpeed(12F)
                .setAtmospherePressure(1000.2344F)
                .setHumidity((short) 50)
                .setWeatherConditions("Mist")
                .setLocation("London")
                .setLastUpdateEpoch(LocalDateTime.now());
        result.add(weatherRecordOne);
        result.add(weatherRecordTwo);
        return result;
    }

}