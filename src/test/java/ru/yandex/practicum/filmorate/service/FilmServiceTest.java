package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;


class FilmServiceTest {

    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserStorage userStorage = new InMemoryUserStorage();

    FilmService filmService = new FilmService(filmStorage);

    private Film film = Film.builder()
            .id(1)
            .name("Фильм")
            .description("Описание")
            .releaseDate(LocalDate.of(2000,10,1))
            .duration(100)
            .build();

    private User user = User.builder()
            .id(1)
            .login("Росомаха")
            .email("email@email.ru")
            .name("Логан")
            .birthday(LocalDate.of(2000,5,10))
            .build();

    private User user2 = User
            .builder()
            .id(2)
            .email("deadpool@email.ru")
            .name("Уэйд")
            .login("Deadpool")
            .birthday(LocalDate.of(1991,1,30))
            .build();

    @Test
    void likeIt() {
        userStorage.addUser(user);
        userStorage.addUser(user2);
        filmStorage.addFilm(film);
       // Set<Integer> set = filmStorage.getLikes(1);
       // set.add(3);
      //  set.add(4);
      //  int size = filmStorage.removeLike(1,set);
        Film film = filmStorage.getFilm(1);

       // filmService.up(film.toBuilder().rate(size).build());
      //  System.out.println(filmStorage.getLikes(1));

        System.out.println("   ." + filmService.getPopular(0));

        System.out.println("  .." + filmService.getPopular(2));

        filmStorage.addFilm(film.toBuilder().id(2).build());
        System.out.println(" ..." + filmService.getPopular(2));
    }
}