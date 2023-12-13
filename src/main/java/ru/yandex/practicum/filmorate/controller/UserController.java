package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    @PutMapping("/{id}/friends/{friendId}") //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public User addToFriends(
            @PathVariable("id") Integer userId,
            @PathVariable Integer friendId
    ) {

        return userService.addToFriends(userId,friendId);
    }

    public void removeFromFriends( //DELETE /users/{id}/friends/{friendId} — удаление из друзей
            @PathVariable("id") Integer userId,
            @PathVariable Integer friendId
    ) {
            userService.removeFromFriends(userId,friendId);
    }

    public List<User> getMyFriends(Integer userId) { //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    return List.of();
    }

    public void getMutualFriends(Integer userId) { //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    }
}
