package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
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
