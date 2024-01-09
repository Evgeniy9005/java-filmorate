package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;

@Component("films")

@RequiredArgsConstructor//создать конструктор, включить все финал поля
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int addFilm(Film film) {
        return 0;
    }

    @Override
    public List<Film> getFilms() {
        return null;
    }

    @Override
    public Film getFilm(int filmId) {
        return null;
    }

    @Override
    public Film removeFilm(Film film) {
        return null;
    }

    @Override
    public int removeLike(int filmId, Set<Integer> setLikes) {
        return 0;
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        return null;
    }
}
