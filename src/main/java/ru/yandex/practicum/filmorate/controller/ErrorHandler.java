package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.exceptions.InputParametersException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final RuntimeException e) {
        return new ErrorResponse(
                e.toString(), "Произошла непредвиденная ошибка."
        );
    }

    @ExceptionHandler
   // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final InputParametersException e) {
        return new ErrorResponse(
                e.toString(), "Произошла ошибка ввода параметров!"
        );
    }

    @ExceptionHandler
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final FriendException e) {
        return new ErrorResponse(
                e.toString(), "Что то перепутали с друзьями!"
        );
    }
}
