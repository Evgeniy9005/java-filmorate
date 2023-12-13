package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

/**
 хранения, обновления и поиска объектов
 */
public interface FilmStorage {

    void addFilm(Film film);

    Film up(Film film);

    List<Film> getFilms();

    public Film getFilm(int filmId);
}
