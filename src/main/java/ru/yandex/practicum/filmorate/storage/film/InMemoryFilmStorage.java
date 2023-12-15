package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

    public boolean isFilm(int id) {
        return filmMap.containsKey(id);
    }

    public Film removeFilm(Film film) {
        Integer id = film.getId();

        if(filmMap.containsKey(id)) {
            return filmMap.remove(id);
        }

        throw new FilmNotFoundException("Не возможно удалить фильм! " + film);

    }


    @Override
    public List<Film> getFilms() {
        log.info("Отправлен список фильмов, в количестве " + filmMap.size());
        return new ArrayList<>(filmMap.values());
    }


    @Override
    public Film getFilm(int filmId) {
        if(!filmMap.containsKey(filmId)) {
            throw new FilmNotFoundException("Идентификатор = "+filmId);
        }

        log.info("Отправлен фильм c id = " + filmId);

        return filmMap.get(filmId);
    }



    public Set<Integer> getLikes(int filmId) {
        if (filmMap.containsKey(filmId)) {
            if(likes.containsKey(filmId)) {
                return new HashSet<>(likes.get(filmId));
            } else {
                likes.put(filmId,new HashSet<>());
                return new HashSet<>(likes.get(filmId));
            }

        }

        throw new FilmNotFoundException("Невозможно вернуть оценки!");
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
