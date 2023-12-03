package org.home.weatherservice;

import lombok.experimental.UtilityClass;
import org.home.weatherservice.dto.weather.response.ConditionDto;
import org.home.weatherservice.dto.weather.response.DataDto;
import org.home.weatherservice.dto.weather.response.LocationDto;
import org.home.weatherservice.dto.weather.response.WeatherDto;
import org.home.weatherservice.entity.Weather;

import java.time.LocalDateTime;

@UtilityClass
public class DataFactory {

    public static WeatherDto buildWeatherDto() {
        return new WeatherDto(
                new LocationDto(
                        "Some name",
                        "Some region",
                        "Some country",
                        1.0F,
                        2.1F,
                        "Some timeZone",
                        System.currentTimeMillis(),
                        LocalDateTime.now()
                ),
                new DataDto(12234, LocalDateTime.now(), 34.23F, 99.0F, 1,
                        new ConditionDto("Mist", 1), 12.3F, 2.3F, 22.4F,
                        "test", 33.0F, 233.3F, (short) 12, 33, 23.5F,
                        43.4F, 12.0F, 43.4F)
        );
    }

    public static Weather buildWeatherEntity(WeatherDto weatherDto) {
        return new Weather()
                .setTemperature(weatherDto.current().tempC())
                .setAtmospherePressure(weatherDto.current().pressureMb())
                .setHumidity(weatherDto.current().humidity())
                .setLastUpdateEpoch(LocalDateTime.parse("1970-01-01T03:23:54"))
                .setLocation(weatherDto.location().name())
                .setWeatherConditions(weatherDto.current().condition().text())
                .setWindSpeed(weatherDto.current().windMph());
    }
}
