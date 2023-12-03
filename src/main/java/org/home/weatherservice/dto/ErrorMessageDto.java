package org.home.weatherservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ErrorMessageDto {
    private HttpStatus status;
    private LocalDateTime date = LocalDateTime.now();
    private String message;
}
