package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InputParametersException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.Util;



import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**Создайте FilmService, который будет отвечать за операции с фильмами, —
 * добавление и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков.
Пусть пока каждый пользователь может поставить лайк фильму только один раз.*/

@Service
public class FilmService {

    @Autowired
    @Qualifier("films")
    private FilmStorage films;

    private static Integer globalId = 0;


    public FilmService(FilmStorage films) {
        this.films = films;
    }

    private static Integer getNextId() {
        return ++globalId;
    }

    public Film create(Film film) {

        // return films.getFilm(films.addFilm(film.toBuilder().id(getNextId()).build()));
        int id = films.addFilm(film);

        return films.getFilm(id);
    }

    public Film up(Film film) {

        /*films.removeFilm(film);

        int id = films.addFilm(film);

        return films.getFilm(id);*/
        return films.updateFilm(film);
    }


    public List<Film> getFilms() {

        List<Film> list = films.getFilms();
        System.out.println("*******************************");
        list.stream().forEach(System.out::println);
        System.out.println("*******************************");
        return list;
    }

    public Film getFilm(Integer filmId) {
        return films.getFilm(filmId);
    }


    public void likeIt(Integer filmId, Integer userId) { // — пользователь ставит лайк фильму.

        Util.valid(filmId,userId);

        /*if (!InMemoryUserStorage.containsUser(userId)) {
            throw new InputParametersException("Пользователь " + userId + " не найден!");
        }

        Set<Integer> set = films.getLikes(filmId);

        set.add(userId);
       // int size = films.removeLike(filmId,set);

        Film film = films.getFilm(filmId);
       // up(film.toBuilder().rate(size).build());*/

        films.likeFilm(filmId,userId);
    }

    public void deleteLike(Integer filmId, Integer userId) { // — пользователь удаляет лайк.
        Util.valid(filmId,userId);

        /*if (!InMemoryUserStorage.containsUser(userId)) {
            throw new InputParametersException("Пользователь " + userId + " не найден!");
        }

        Set<Integer> set = films.getLikes(filmId);

        if (set.contains(userId)) {
            set.remove(userId);
        }

       // int size = films.removeLike(filmId,set);
        Film film = films.getFilm(filmId);
      //  up(film.toBuilder().rate(size).build());*/

        films.removeLike(filmId,userId);
    }

    /*Возвращает список из первых count фильмов по количеству лайков.
      Если значение параметра count не задано, вернет первые 10.*/
    public List<Film> getPopular(Integer count) {
        int size = films.getFilms().size();
        if (count == null || count <= 0) {
            count = 10;
        }

        if (size < count) {
            count = size;
        }

        List<Film> list = films.getFilms().stream()
                .sorted((f1, f2) -> compare(f1.getRate(),f2.getRate()))
                .skip(0)
                .limit(count)
                .collect(Collectors.toList());
        return list;
    }

    private int compare(Integer f0, Integer f1) {
        int result = f1.compareTo(f0); // порядок сортировки по возрастанию
        return result;
    }

    public Genre getGenre(Integer genreId) {
        Util.valid(genreId);
        return films.getGenre(genreId);
    }

    public List<Genre> getGenres() {
        return films.getGenres();
    }

    public Mpa getMPA(Integer mpaId) {
        Util.valid(mpaId);
        return films.getMPA(mpaId);
    }

    public List<Mpa> getAllMPA() {
        return films.getAllMPA();
    }
}
