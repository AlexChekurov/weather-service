package org.home.weatherservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.home.weatherservice.clients.WeatherApiFeignClient;
import org.home.weatherservice.dto.api.response.AggregatedWeatherApiResponseDto;
import org.home.weatherservice.dto.api.response.WeatherApiResponseDto;
import org.home.weatherservice.dto.weather.response.WeatherDto;
import org.home.weatherservice.exception.NoDataForLocationException;
import org.home.weatherservice.mapper.WeatherMapper;
import org.home.weatherservice.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.home.weatherservice.mapper.WeatherMapper.DEFAULT_BIG_DECIMAL_SCALE;
import static org.home.weatherservice.mapper.WeatherMapper.DEFAULT_ROUNDING_MODE;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeatherService {

    @Value("${wheather-app.location}")
    private String defaultLocation;

    private final WeatherApiFeignClient weatherFeignClient;

    private final WeatherRepository weatherRepository;

    private final WeatherMapper weatherMapper;

    public void updateWeather() {
        var currentWeather = getWeatherFromApi();
        persistResponse(currentWeather);
    }

    public WeatherDto getWeatherFromApi() {
        try {
            log.info("Запрашиваем погоду для {}", defaultLocation);
            var currentWeather = weatherFeignClient.getCurrentWeather(defaultLocation);
            log.debug("Текущая погода для {}: {}", defaultLocation, currentWeather);
            return currentWeather;
        } catch (Exception ex) {
            log.error("Не удалось получить данные из сервиса погоды для {}", defaultLocation, ex);
            return null;
        }
    }

    public WeatherApiResponseDto findLastWeatherData(String location) {
        var targetLocation = (location == null || location.trim().isBlank()) ? defaultLocation : location.trim();
        try {
            log.info("Загружаем последниеданные данные о погоде для {}", targetLocation);
            var result = weatherRepository.findFirstByLocationOrderByLastUpdateEpochDesc(targetLocation)
                    .map(weatherMapper::toApiResponseDto)
                    .orElseThrow(() -> new NoDataForLocationException(targetLocation));
            log.debug("Последние данные о погоде: {}", result);
            return result;
        } catch (Exception ex) {
            log.error("Не удалось получить данные из базы {}", targetLocation, ex);
            throw ex;
        }
    }

    public AggregatedWeatherApiResponseDto findAggregatedWeatherData(String location, LocalDate from, LocalDate to) {
        var targetLocation = (location == null || location.trim().isBlank()) ? defaultLocation : location.trim();
        try {
            log.info("Загружаем аггрегированные данные о погоде для {} за период {} - {}", targetLocation, from, to);

            var result = weatherRepository.findAvgWeatherByLocationAndPeriod(targetLocation, from.atStartOfDay(), to.atStartOfDay())
                    .map(weatherMapper::toApiAggregatedResponseDto)
                    .orElseThrow(() -> new NoDataForLocationException(targetLocation));
            if (!isDataPresents(result)) {
                log.error("Получен ответ с null-полем: {}", result);
                throw new NoDataForLocationException(targetLocation);
            }
            log.debug("Аггрегированные данные о погоде: {}", result);
            return result;
        } catch (Exception ex) {
            log.error("Не удалось получить аггрегированные данные из базы {}", targetLocation, ex);
            throw ex;
        }
    }

    private void persistResponse(WeatherDto dto) {
        if (dto == null) {
            log.warn("Отсутствую данные о погоде!");
            return;
        }
        try {
            var entity = weatherMapper.mapToEntity(dto);

            var entityExists = weatherRepository.existsByLocationAndLastUpdateEpoch(
                    entity.getLocation(),
                    entity.getLastUpdateEpoch());
            if (entityExists) {
                log.info("Данные уже записаны ранее, пропускаем запись.");
                return;
            }
            weatherRepository.save(entity);
            log.info("Успешно сохранен ответ от сервиса");
        } catch (Exception exception) {
            log.error("Не удалось сохранить данные о погоде", exception);
        }
    }

    private boolean isDataPresents(AggregatedWeatherApiResponseDto aggregatedDto) {
        var zero = BigDecimal.ZERO.setScale(DEFAULT_BIG_DECIMAL_SCALE, DEFAULT_ROUNDING_MODE);

        return aggregatedDto.getAvgHumidity().compareTo(zero) != 0
                && aggregatedDto.getAvgTemperature().compareTo(zero) != 0
                && aggregatedDto.getAvgWindSpeed().compareTo(zero) != 0
                && aggregatedDto.getAvgAtmospherePressureMBar().compareTo(zero) != 0;

    }


}
