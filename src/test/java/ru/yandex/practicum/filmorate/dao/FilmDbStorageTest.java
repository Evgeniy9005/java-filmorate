package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmStorage films;

    private UserStorage users;

    private Film[] film = new Film[6];

    private User[] user = new User[6];

    @BeforeEach
    void start() {
        films = new FilmDbStorage(this.jdbcTemplate);

        for(int i = 1; i < film.length+1; i++) {
            film[i-1] = Film.builder()
                    .id(i)
                    .name("film"+i)
                    .description("Описание"+i)
                    .releaseDate(LocalDate.now())
                    .duration(100+i)
                    .rate(0)
                    .genres(new HashSet<>())
                    .mpa(new Mpa(1,"G"))
                    .build();
            System.out.println(film[i-1]);
        }

        users = new UserDbStorage(this.jdbcTemplate);
        for(int i = 1; i < user.length+1; i++) {
            user[i-1] = User.builder()
                    .id(i)
                    .login("login"+i)
                    .email("email"+i)
                    .name("name"+i)
                    .birthday(LocalDate.of(1990, 1, 1))
                    .friends(Set.of())
                    .build();
            System.out.println(user[i-1]);
        }

        users.addUser(user[0]);
        users.addUser(user[1]);
        users.addUser(user[2]);
        users.addUser(user[3]);
        users.addUser(user[4]);
        users.addUser(user[5]);
    }


    @Test
    void addGetFilm() {

        assertEquals(1,films.addFilm(film[0]),"добавить фильм 1 в бд");
        assertEquals(2,films.addFilm(film[1]),"добавить фильм 2 в бд");
        assertEquals(3,films.addFilm(film[2]),"добавить фильм 3 в бд");
        assertEquals(4,films.addFilm(film[3]),"добавить фильм 4 в бд");
        assertEquals(5,films.addFilm(film[4]),"добавить фильм 5 в бд");
        assertEquals(6,films.addFilm(film[5]),"добавить фильм 6 в бд");

        assertIterableEquals(List.of(film),films.getFilms(),"вернуть все фильмы");


        assertIterableEquals(List.of(new Genre(1,"Комедия"),
                new Genre(2,"Драма"),
                new Genre(3,"Мультфильм"),
                new Genre(4,"Триллер"),
                new Genre(5,"Документальный"),
                new Genre(6,"Боевик")),films.getGenres(),"вернуть все жанры");

        assertIterableEquals(List.of(new Mpa(1,"G"),
                new Mpa(2,"PG"),
                new Mpa(3,"PG-13"),
                new Mpa(4,"R"),
                new Mpa(5,"NC-17")),films.getAllMPA(),"вернуть все рейтинги");

        Set<Genre> genresSet = new HashSet<>();
        genresSet.add(films.getGenre(1));
        genresSet.add(films.getGenre(3));

        Film filmUp = Film.builder()
                .id(1)
                .name("filmUp")
                .description("ОписаниеUp")
                .releaseDate(LocalDate.now())
                .duration(120)
                .rate(2)
                .genres(genresSet)
                .mpa(films.getMPA(1))
                .build();

        films.likeFilm(1,1);
        assertEquals(1,films.getFilm(1).getRate(),"получить количество оценок 1");
        assertEquals("Сбой при запросе оценки фильма!"
                ,assertThrows(FilmException.class, ()->
                        films.likeFilm(1,1) //добавить оценку от того-же пользователя еще раз
                ).getMessage());
        films.likeFilm(1,2);
        films.likeFilm(1,3);
        assertEquals(3,films.getFilm(1).getRate(),"получить количество оценок 3");
        assertEquals(1,films.removeLike(1,1),"удалить оценку пользователем");
        assertEquals(0,films.removeLike(1,1),"удалить оценку пользователем еще раз");
        assertEquals(2,films.getFilm(1).getRate(),"получить количество оценок 2");

        assertEquals(genresSet,films.updateFilm(filmUp).getGenres(),"получить список жанров фильма");
        assertEquals(films.getFilm(filmUp.getId()),films.updateFilm(filmUp), "обновить фильм еще раз подряд");

        films.removeFilm(filmUp); //удалить фильм
        assertEquals("Сбой при получении фильма из базы данных!"
                ,assertThrows(FilmException.class, ()->
                        films.getFilm(filmUp.getId())//удален фильм
                ).getMessage());

    }


    @Test
    void addGetGenre() {

        assertEquals("Жанра фильма не найден! id=9999"
                ,assertThrows(GenreException.class, ()->
                        films.getGenre(9999)
                ).getMessage());

        assertEquals("Сбой запроса при добавлении жанра! Название \"Комедия\" уже есть"
                ,assertThrows(GenreException.class, ()->
                        films.addGenre("Комедия")
                ).getMessage());
    }

    @Test
    void addGetMPA() {

        assertEquals("Рейтинг фильма не найден! id=9999"
                ,assertThrows(RatingException.class, ()->
                        films.getMPA(9999)
                ).getMessage());


        assertEquals("Сбой запроса при добавлении рейтинга! Название \"G\" уже есть"
                ,assertThrows(RatingException.class, ()->
                        films.addMPA("G")
                ).getMessage());

    }


    @Test
    void addUser() {

        assertEquals(user[0],users.getUser(1),"найден пользователь 1");
        assertEquals(user[0],users.getUser(user[0]),"найден пользователь 1");
        assertEquals(user[5],users.getUser(user[5]),"найден пользователь 5");

        assertIterableEquals(List.of(user), users.getUsers(),"вернуть всех пользователей");

        users.removeUser(user[5]);
        assertEquals("Пользователь не найден id = 6"
                ,assertThrows(UserNotFoundException.class, ()->
                        users.getUser(user[5])
                ).getMessage());

        users.getUsers().stream().forEach(System.out::println);

        assertTrue(users.isUser(1),"есть пользователь 1?");
        assertTrue(users.isUser(2),"есть пользователь 2?");
        assertTrue(users.isUser(3),"есть пользователь 3?");

        assertEquals(2,users.addToFriends(1,2).getId(),
                "Пользователь 1 подал заявку в друзья к пользователю 2");
        assertEquals(3,users.addToFriends(1,3).getId(),
                "Пользователь 1 подал заявку в друзья к пользователю 3");
        assertEquals(4,users.addToFriends(1,4).getId(),
                "Пользователь 1 подал заявку в друзья к пользователю 4");

        assertEquals("Пользователь не найден id = 9999"
                ,assertThrows(UserNotFoundException.class, ()->
                        users.addToFriends(9999,9998)
                ).getMessage());


        assertTrue(users.iAgreeFriend(2,1),"Пользователь 2 подтвердил заявку в друзья от пользователя 1");
        assertFalse(users.iAgreeFriend(2,1),"Пользователь 2 подтвердил заявку в друзья от пользователя 1 второй раз");
        assertTrue(users.iAgreeFriend(3,1),"Пользователь 3 подтвердил заявку в друзья от пользователя 1 порядок ввода параметров не важен");


        assertIterableEquals(List.of(2,3,4), users.getUser(1).getFriends(),"у пользователя 1 должен быть друг 2 и 3");
        assertIterableEquals(List.of(1), users.getUser(2).getFriends(),"у пользователя 2 должен быть друг 1");
        assertIterableEquals(List.of(1), users.getUser(3).getFriends(),"у пользователя 3 должен быть друг 1");
        assertIterableEquals(List.of(), users.getUser(4).getFriends(),"у пользователя 4 не должно быть друзей");

        User userUp = users.getUser(1).toBuilder().login("loginUp")
                .email("emailUp")
                .name("nameUp")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertEquals(userUp,users.updateUser(userUp),"проверка обновления пользователя");


        users.removeFromFriends(1,2);
        assertEquals("Пользователи не в друзьях"
                ,assertThrows(FriendException.class, ()->
                        users.removeFromFriends(1,2) //проверка, что было произведено удаление из друзей
                ).getMessage());

    }
}