package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final InputParametersException e) {
        log.debug("Получен статус 404 Not found {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(), e.getMessage() + " Ошибка ввода параметров!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final FriendException e) {
        log.debug("Получен статус 404 Not found {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(), e.getMessage() + " Неразбериха в отношениях пользователей!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final UserNotFoundException e) {
        log.debug("Получен статус 404 Not found {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(),  e.getMessage() + " Пользователь не найден!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final FilmNotFoundException e) {
        log.debug("Получен статус 404 Not found {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(),  e.getMessage() + " Фильм не найден!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final RuntimeException e) {
        log.debug("Получен статус 500 internal server error {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(), "Произошла непредвиденная ошибка!"
        );
    }
}
