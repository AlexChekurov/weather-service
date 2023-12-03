package org.home.weatherservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Weather {
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "weatherRecordSecGenerator")
    @SequenceGenerator(name = "weatherRecordSecGenerator", sequenceName = "weather_record_id_sec", allocationSize = 1)
    public Long id;

    @NotNull
    public Float temperature;

    @NotNull
    public Float windSpeed;

    @NotNull
    public Float atmospherePressure;

    @NotNull
    public Short humidity;

    @NotBlank
    public String weatherConditions;

    @NotBlank
    public String location;

    @NotNull
    public LocalDateTime lastUpdateEpoch;

}
