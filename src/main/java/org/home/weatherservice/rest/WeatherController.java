package org.home.weatherservice.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.home.weatherservice.dto.api.request.AggregationPeriodRequest;
import org.home.weatherservice.dto.api.response.AggregatedWeatherApiResponseDto;
import org.home.weatherservice.dto.api.response.WeatherApiResponseDto;
import org.home.weatherservice.service.WeatherService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/weather")
@AllArgsConstructor
public class WeatherController {
    WeatherService weatherService;

    @GetMapping
    public WeatherApiResponseDto getLastWeatherData(
            @RequestParam(required = false) String location) {
        return weatherService.findLastWeatherData(location);
    }

    @PostMapping
    @Validated
    public AggregatedWeatherApiResponseDto getAverageWeatherData(
            @RequestParam(required = false) String location,
            @RequestBody @Valid AggregationPeriodRequest request) {
        return weatherService.findAggregatedWeatherData(location, request.getFrom(), request.getTo());
    }

}
