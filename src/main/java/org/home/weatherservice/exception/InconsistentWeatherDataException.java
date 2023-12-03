package org.home.weatherservice.exception;

import org.home.weatherservice.dto.weather.response.DataDto;
import org.home.weatherservice.dto.weather.response.LocationDto;

public class InconsistentWeatherDataException extends RuntimeException {
    public InconsistentWeatherDataException(LocationDto locationDto, DataDto dataDto) {
        super("Получены неполные данные о погоде: location = " + locationDto + ", dataDto = " + dataDto);
    }
}
