package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private Map<Integer,Film> filmMap = new HashMap<>();
    private int id = 0;

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        id++;
        film.setId(id);
        filmMap.put(id,film);
        log.info("Добавлен фильм= " + film);
    return filmMap.get(id);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film film) {
        int idFilm = film.getId();

        if (filmMap.containsKey(idFilm)) {
            filmMap.remove(idFilm);
            filmMap.put(idFilm,film);
            log.info("Обновлен фильм= " + film);
            return filmMap.get(idFilm);
        } else {
            throw new ValidationException("Токого фильма нет " + film);
        }
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Отправлен список фильмов, в количестве " + filmMap.size());
    return new ArrayList<>(filmMap.values());
    }
}
