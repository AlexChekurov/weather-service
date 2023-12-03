package org.home.weatherservice.exception;

public class NoDataForLocationException extends RuntimeException {
    public NoDataForLocationException(String location) {
        super("Нет данных о погоде для " + location);
    }
}
