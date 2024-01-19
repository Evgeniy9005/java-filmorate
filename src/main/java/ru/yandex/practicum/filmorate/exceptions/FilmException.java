package ru.yandex.practicum.filmorate.exceptions;

public class FilmException extends RuntimeException {
    private String exception;

    public FilmException(String message, String exception) {
        super(message);
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }
}
