package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody  User user) {
        return userService.up(user);
    }

    @GetMapping()
    public List<User> getUsers() {
        log.info("Отправлен список пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(// - вернуть пользователя по идентификатору
        @PathVariable("id") Integer userId
    ) {
        return userService.getUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}") // — добавление в друзья.
    public User addToFriends(
        @PathVariable("id") Integer userId,
        @PathVariable Integer friendId
    ) {
        return userService.addToFriends(userId,friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(// — удаление из друзей
        @PathVariable("id") Integer userId,
        @PathVariable Integer friendId
    ) {
            userService.removeFromFriends(userId,friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getMyFriends(// — возвращаем список пользователей, являющихся его друзьями.
        @PathVariable("id") Integer userId
    ) {
        System.out.println("------------ "+userId);
        return userService.getMyFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(// — список друзей, общих с другим пользователем.
        @PathVariable("id") Integer userId,
        @PathVariable Integer otherId
    ) {
        return userService.getMutualFriends(userId, otherId);
    }
}
