package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

/**
 хранения, обновления и поиска объектов
 */
public interface UserStorage {

    void addUser(User user);

    User removeUser(User user);

    List<User> getUsers();

    User getUser(User user);

    User getUser(int id);

    boolean isUser(int id);

}
