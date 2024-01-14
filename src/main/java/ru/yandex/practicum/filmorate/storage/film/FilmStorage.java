package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

/**
 хранения, обновления и поиска объектов
 */
public interface FilmStorage {

    int addFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int filmId);

    Film updateFilm(Film film);

    Film removeFilm(Film film);

    int removeLike(int filmId, int userId);

    Set<Integer> getLikes(int filmId);

    void likeFilm(Integer filmId, Integer userId);

    Genre addGenre(String genre);

    void deleteGenre(Genre genre);

    Genre updateGenre(Genre genre);

    List<Genre> getGenres();

    Genre getGenre(int genreId);

    Mpa addMPA(String mpa);

    void deleteMPA(Mpa mpa);

    Mpa updateMPA(Mpa mpa);

    List<Mpa> getAllMPA();

    Mpa getMPA(int mpaId);

}
