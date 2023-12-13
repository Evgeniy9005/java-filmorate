package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**Создайте FilmService, который будет отвечать за операции с фильмами, —
 * добавление
 * и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков.
Пусть пока каждый пользователь может поставить лайк фильму только один раз.*/
@Service
public class FilmService {

    private FilmStorage films;

    private UserStorage users;

    @Autowired
    public FilmService(FilmStorage films, UserStorage users) {
        this.films = films;
        this.users = users;
    }

    public void likeIt(int filmId, int userId) { //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.

        Film film = films.getFilm(filmId);
    film.setLike(userId);
    films.up(film);
    }

    public void deleteLike(int filmId, int userId) { //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
        Film film = films.getFilm(filmId);
        film.deleteLike(userId);
        films.up(film);
    }

    /*GET /films/popular?count={count} — возвращает список из первых count фильмов
    по количеству лайков. Если значение параметра count не задано, верните первые 10.*/
    public List<Film> getPopular(int count) {
        return films.getFilms().stream()
                .sorted((f1, f2) -> compare(f1,f2))
                .skip(0)
                .limit(count < 10 ? 10 : count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        int result = f0.getNumberLikes().compareTo(f1.getNumberLikes()); //прямой порядок сортировки
        return result;
    }

}
