package org.home.weatherservice.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import static org.home.weatherservice.constant.CommonConstant.DEFAULT_DATE_FORMAT;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AggregationPeriodRequest {
    @NotNull
    @JsonFormat(pattern = DEFAULT_DATE_FORMAT)
    //тип указан строкой, так как swagger пример для даты берет из LocalDateTime и игнорирует формат
    @Schema(type = "string", example = "02-12-2023")
    private LocalDate from;

    @NotNull
    @JsonFormat(pattern = DEFAULT_DATE_FORMAT)
    //тип указан строкой, так как swagger пример для даты берет из LocalDateTime и игнорирует формат
    @Schema(type = "string", example = "31-12-2023")
    private LocalDate to;
}
