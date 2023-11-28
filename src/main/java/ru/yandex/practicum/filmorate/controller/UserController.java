package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private Map<Integer, User> userMap = new HashMap<>();
    private int id = 0;

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        id++;
        user.setId(id);
        userMap.put(id,noName(user));
        log.info("Добавлен пользователь= " + user);
        return userMap.get(id);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody @Valid User user) {
        int idUser = user.getId();

        if (userMap.containsKey(idUser)) {
            userMap.remove(idUser);
            userMap.put(idUser,user);
            log.info("Обновлен пользователь= " + user);
            return userMap.get(idUser);
        } else {
            throw new ValidationException("Токого пользователя нет " + user);
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Отправлен список пользователей, в количестве " + userMap.size());

        return new ArrayList<>(userMap.values());
    }

    private User noName(User user) {
        String name = user.getName();

        if(name == null  || name.isBlank() || name.isEmpty()) {
            return user.toBuilder().name(user.getLogin()).build();
        }
        return user;
    }
}
