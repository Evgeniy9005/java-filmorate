package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("checkstyle:Regexp")
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Входной параметр метода создания " + film);
        return filmService.create(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Входной параметр метода обновления " + film);
        return filmService.up(film);
    }

    @GetMapping()
    public List<Film> getFilms() {
    return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(
           @PathVariable("id") Integer filmId
    ) {
        return filmService.getFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")//PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму
    public void likeIt(
           @PathVariable("id") Integer filmId,
           @PathVariable() Integer userId
    ) {
        log.info(filmId + ", " + userId + " - входные параметры метода добовления like ");
        filmService.likeIt(filmId,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")//DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public void deleteLike(
           @PathVariable("id") Integer filmId,
           @PathVariable() Integer userId
    ) {
        log.info(filmId + ", " + userId + " - входные параметры метода удаление likes ");
        filmService.deleteLike(filmId,userId);
    }

    /*GET /films/popular?count={count} — возвращает список из первых count фильмов
    по количеству лайков. Если значение параметра count не задано, вернет первые 10.*/
    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(defaultValue = "10", required = false) Integer count
    ) {
        log.info(count + " - входной параметр метода получения списка фильмов по популярности");

        return filmService.getPopular(count);
    }
}
