package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.exceptions.InputParametersException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final InputParametersException e) {
        return new ErrorResponse(
                e.getClass().getName(), e.getMessage() + " Ошибка ввода параметров!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final FriendException e) {
        return new ErrorResponse(
                e.getClass().getName(), e.getMessage() + " Неразбериха в отношениях пользователей!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final UserNotFoundException e) {
        return new ErrorResponse(
                e.getClass().getName(),  e.getMessage() + " Пользователь не найден!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final FilmNotFoundException e) {
        return new ErrorResponse(
                e.getClass().getName(),  e.getMessage() + " Фильм не найден!"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final RuntimeException e) {
        return new ErrorResponse(
                e.getClass().getName(), "Произошла непредвиденная ошибка!"
        );
    }
}
