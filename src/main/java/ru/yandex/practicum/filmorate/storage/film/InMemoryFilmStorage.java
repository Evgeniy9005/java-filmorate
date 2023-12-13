package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** имплементирующие новые интерфейсы,
  и перенесите туда всю логику хранения,
  обновления и поиска объектов.*/
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> filmMap = new HashMap<>();
    private int id = 0;

    @Override
    public void addFilm(Film film) {
        id++;
        filmMap.put(id,film.toBuilder().id(id).build());
        filmMap.put(id,film);
      //  log.info("Добавлен фильм= " + film);
    }

    @Override
    public Film up(Film film) {
        int idFilm = film.getId();

        if (filmMap.containsKey(idFilm)) {
            filmMap.remove(idFilm);
            filmMap.put(idFilm,film);
          //  log.info("Обновлен фильм= " + film);
            return filmMap.get(idFilm);
        } else {
            throw new ValidationException("Токого фильма нет " + film);
        }
    }

    @Override
    public List<Film> getFilms() {
       // log.info("Отправлен список фильмов, в количестве " + filmMap.size());
        return new ArrayList<>(filmMap.values());
    }


    @Override
    public Film getFilm(int filmId) {
        return filmMap.get(filmId);
    }

}
