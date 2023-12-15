package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
            .email("deadpool@email.ru")
            .name("Уэйд")
            .login("Deadpool")
            .birthday(LocalDate.of(1991,1,30))
            .friends(new HashSet<>())
            .build();

    @Test
    void create() {
        userService.create(user);
        userService.create(user2);
        System.out.println();
        userService.getUsers().stream().forEach(System.out::println);
        System.out.println(user.getFriends().getClass());

        User user3 = userService.getUser(1);
        User user4 = userService.getUser(2);

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