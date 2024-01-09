package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;


@Component
class UserServiceTest {
    UserStorage userStorage = new InMemoryUserStorage();

    UserService userService = new UserService(userStorage);


    private User user = User.builder()
            .id(1)
            .login("Росомаха")
            .email("email@email.ru")
            .name("Логан")
            .birthday(LocalDate.of(2000,5,10))
            .friends(new HashSet<>())
            .build();

    private User user1 = user.toBuilder().name("").build();

    private User user2 = User
            .builder()
            .id(2)
            .email("deadpool@email.ru")
            .name("Уэйд")
            .login("Deadpool")
            .birthday(LocalDate.of(1991,1,30))
            .friends(new HashSet<>())
            .build();


    @Test
    void create() {
        User user3 = userService.create(user);
        User user4 = userService.create(user2);
        System.out.println();
        userService.getUsers().stream().forEach(System.out::println);
        System.out.println(user.getFriends().getClass());


        userService.addToFriends(user3.getId(),user4.getId());

        System.out.println();
        userService.getUsers().stream().forEach(System.out::println);

        userService.removeFromFriends(user3.getId(),user4.getId());

        System.out.println();
        userService.getUsers().stream().forEach(System.out::println);
    }

    @Test
    void addToFriends() {
    }

}