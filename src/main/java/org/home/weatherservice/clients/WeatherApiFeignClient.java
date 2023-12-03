package org.home.weatherservice.clients;

import org.home.weatherservice.dto.weather.response.WeatherDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherFeign")
public interface WeatherApiFeignClient {

    @RequestMapping(method = RequestMethod.GET, consumes = "application/json")
    WeatherDto getCurrentWeather(@RequestParam(value = "q") String location);

}
