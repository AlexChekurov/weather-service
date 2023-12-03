package org.home.weatherservice.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.home.weatherservice.service.WeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class SchedulerService {

    private final WeatherService weatherService;

    @Scheduled(cron = "${wheather-app.cron}")
    public void loadWeather() {
        log.info("Старт периодической загрузки данных о погоде");
        weatherService.updateWeather();
        log.info("Периодическая загрузка завершена");
    }
}
