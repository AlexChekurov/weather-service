package org.home.weatherservice.rest;

import lombok.extern.slf4j.Slf4j;
import org.home.weatherservice.dto.ErrorMessageDto;
import org.home.weatherservice.exception.NoDataForLocationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MSG = "Внутренняя ошибка сервиса";

    @ExceptionHandler(value = NoDataForLocationException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessageDto resourceNotFoundException(NoDataForLocationException ex, WebRequest request) {
        log.error("Ошибка при обработке запроса: {}", request, ex);
        return new ErrorMessageDto()
                .setStatus(HttpStatus.NOT_FOUND)
                .setMessage(ex.getMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessageDto humanReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Ошибка при обработке запроса: {}", request, ex);
        return new ErrorMessageDto()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setMessage(ex.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessageDto invalidArgumentsException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Ошибка при обработке запроса: {}", request, ex);
        var error = ex.getFieldErrors().stream()
                .map(fieldError -> "Поле " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(". "));
        return new ErrorMessageDto()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setMessage(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto globalExceptionHandler(Exception ex, WebRequest request) {
        log.error("Ошибка при обработке запроса: {}", request, ex);
        return new ErrorMessageDto()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setMessage(DEFAULT_ERROR_MSG);
    }
}