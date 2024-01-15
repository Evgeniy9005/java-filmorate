package ru.yandex.practicum.filmorate.exceptions;

public class UserException extends RuntimeException {
    String exception;

    public UserException(String message, String exception) {
        super(message);
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }
}