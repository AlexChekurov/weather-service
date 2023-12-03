package org.home.weatherservice.repository;

import org.home.weatherservice.dto.AverageWeatherDataDto;
import org.home.weatherservice.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

    boolean existsByLocationAndLastUpdateEpoch(String location, LocalDateTime lastUpdateEpoch);

    Optional<Weather> findFirstByLocationOrderByLastUpdateEpochDesc(String location);

    @Query("""
            select new org.home.weatherservice.dto.AverageWeatherDataDto(
                avg(w.temperature),
                avg(w.windSpeed),
                avg(w.atmospherePressure),
                avg(w.humidity)
            )
            from Weather w
            where location = :location
                and lastUpdateEpoch between :from and :to
            """)
    Optional<AverageWeatherDataDto> findAvgWeatherByLocationAndPeriod(String location, LocalDateTime from, LocalDateTime to);

}
