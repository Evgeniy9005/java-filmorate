package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 хранения, обновления и поиска объектов
 */
public interface UserStorage {

    int addUser(User user);

    User removeUser(User user);

    List<User> getUsers();

    User getUser(User user);

    User getUser(int id);

    boolean isUser(int id);

    boolean iAgreeFriend(int userId, int friendId);

    User addToFriends(int userId, int friendId);

    void removeFromFriends(int userId, int friendId);

    User updateUser(User user);

}
