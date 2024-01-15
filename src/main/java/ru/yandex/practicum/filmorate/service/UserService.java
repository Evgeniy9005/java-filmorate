package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.Util;

import java.util.List;
import java.util.stream.Collectors;


/**Создайте UserService, который будет отвечать за такие операции с пользователями,
 - как добавление в друзья,
 - удаление из друзей,
 - вывод списка общих друзей.
  Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
  То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.*/

@Slf4j
@Service
public class UserService {
    @Autowired
    @Qualifier("users")
    private UserStorage users;

    public UserService(UserStorage users) {
        this.users = users;
    }


    public User create(User user) {
        User newUser = noName(user);
        int id = users.addUser(newUser);
        return users.getUser(id);
    }

    public User up(User user) {

        User userUp = users.updateUser(noName(user));

        log.info("Обновлен пользователь= " + userUp);

        return users.getUser(userUp);
    }

    public List<User> getUsers() {
        return users.getUsers();
    }


    public User getUser(Integer userId) {
        Util.valid(userId);

        return users.getUser(userId);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public User addToFriends(Integer userId, Integer friendId) {

        log.info("Пользователи {} и {} добавляются друг другу в друзья",userId,friendId);

        if (userId == friendId) {
            throw new FriendException("Вы и так себе друг! Добавьте в друзья кого-то из пользователей!");
        }

        Util.valid(userId, friendId); // проверка входных параметров

        User friend = users.addToFriends(userId,friendId); //добавить в друзья

        return friend;
    }

    public void removeFromFriends(Integer userId, Integer friendId) { //DELETE /users/{id}/friends/{friendId} — удаление из друзей.

        Util.valid(userId, friendId); // проверка входных параметров

        users.removeFromFriends(userId,friendId);

    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    public List<User> getMyFriends(Integer userId) {

        Util.valid(userId);

        return users.getUser(userId).getFriends().stream()
                .map(fId -> {
                    User user = users.getUser(fId);
                    log.info("Возвращает друга {} пользователя {}",user,userId);
                    return user;
                })
                .collect(Collectors.toList());
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        Util.valid(id,otherId);

        return users.getUser(id).getFriends().stream()
                .filter(uId -> users.getUser(otherId).getFriends().contains(uId))
                .map(uId -> users.getUser(uId))
                .collect(Collectors.toList());
    }

     private User noName(User user) {
        String name = user.getName();

        if (name == null  || name.isBlank() || name.isEmpty()) {
            return user.toBuilder().name(user.getLogin()).build();
        }

        return user;
    }
}
