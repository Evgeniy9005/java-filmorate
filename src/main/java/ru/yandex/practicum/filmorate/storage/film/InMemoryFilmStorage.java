package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

/** имплементирующие новые интерфейсы,
  и перенесите туда всю логику хранения,
  обновления и поиска объектов.*/
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> filmMap = new HashMap<>();

    private Map<Integer, Set<Integer>> likes = new HashMap<>(); //id film и id users

    @Override
    public int addFilm(Film film) {
        int id = film.getId();
        filmMap.put(id,film);
        log.info("Добавлен фильм = " + film);
        return id;
    }

    public Film removeFilm(Film film) {
        Integer id = film.getId();

        if (filmMap.containsKey(id)) {
            return filmMap.remove(id);
        }

        throw new FilmNotFoundException("Не возможно удалить фильм! " + film);

    }

    @Override
    public int removeLike(int filmId, int userId) {
    return 0;
    }


    @Override
    public List<Film> getFilms() {
        log.info("Отправлен список фильмов, в количестве " + filmMap.size());
        return new ArrayList<>(filmMap.values());
    }


    @Override
    public Film getFilm(int filmId) {
        if (!filmMap.containsKey(filmId)) {
            throw new FilmNotFoundException("Идентификатор = " + filmId);
        }

        log.info("Отправлен фильм c id = " + filmId);

        return filmMap.get(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }


    public Set<Integer> getLikes(int filmId) {
        if (filmMap.containsKey(filmId)) {
            if (likes.containsKey(filmId)) {
                return new HashSet<>(likes.get(filmId));
            } else {
                likes.put(filmId,new HashSet<>());
                return new HashSet<>(likes.get(filmId));
            }

        }

        throw new FilmNotFoundException("Невозможно вернуть оценки!");
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {

    }

    @Override
    public Genre addGenre(String genre) {
        return null;
    }

    @Override
    public void deleteGenre(Genre genre) {

    }

    @Override
    public Genre updateGenre(Genre genre) {
        return null;
    }

    @Override
    public List<Genre> getGenres() {
        return null;
    }

    @Override
    public Genre getGenre(int genreId) {
        return null;
    }

    @Override
    public Mpa addMPA(String mpa) {
        return null;
    }

    @Override
    public void deleteMPA(Mpa mpa) {

    }

    @Override
    public Mpa updateMPA(Mpa mpa) {
        return null;
    }

    @Override
    public List<Mpa> getAllMPA() {
        return null;
    }

    @Override
    public Mpa getMPA(int mpaId) {
        return null;
    }


    public int removeLike(int filmId, Set<Integer> setLikes) {
        if (likes.containsKey(filmId)) {

            likes.remove(filmId);

            likes.put(filmId,setLikes);

            log.info("Отправлен список списков лайков removeLike = " + likes.values());
            return likes.get(filmId).size();
        }

        throw new FilmNotFoundException("Невозможно удалить оценку!");

    }



}
