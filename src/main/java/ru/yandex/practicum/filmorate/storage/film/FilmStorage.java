package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 хранения, обновления и поиска объектов
 */
public interface FilmStorage {

    int addFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int filmId);

    Film removeFilm(Film film);

    int removeLike(int filmId, Set<Integer> setLikes);

    Set<Integer> getLikes(int filmId);
}
