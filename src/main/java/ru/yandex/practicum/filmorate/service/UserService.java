package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.Util;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private static Integer globalId = 0;


    public UserService(UserStorage users) {
        this.users = users;
    }

    private static Integer getNextId() {
        return ++globalId;
    }

    public User create(User user) {
        User newUser = noName(user,getNextId());
        users.addUser(newUser);
        log.info("В сервисе! Добавлен пользователь = " + newUser);
        return users.getUser(newUser);
    }

    public User up(User user) {
        int id = user.getId();

        users.removeUser(user);
        User upUser = noName(user,id);
        users.addUser(upUser);
        log.info("Обновлен пользователь= " + upUser);

        return users.getUser(upUser);
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
        if (userId == friendId) {
            throw new FriendException("Вы и так себе друг! Добавьте в друзья кого-то из пользователей!");
        }

       // users.iAgreeFriend()

        Util.valid(userId, friendId); // проверка входных параметров

        up(users.getUser(userId).toBuilder().friend(friendId).build());

        //создает и обновляет пользователя возвращает добавленного пользователя в друзья друга
        return up(users.getUser(friendId).toBuilder().friend(userId).build());

    }

    public void removeFromFriends(Integer userId, Integer friendId) { //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
        Util.valid(userId, friendId); // проверка входных параметров

        User user = users.getUser(userId);
        User friend = users.getUser(friendId);

            Set<Integer> setFriendsUser = new HashSet<>(user.getFriends());

            Set<Integer> setFriendsFriend = new HashSet<>(friend.getFriends());

            if (setFriendsUser.contains(friendId)) {

                setFriendsUser.remove(friendId);//удалить из друзей

                up(user.toBuilder().clearFriends().friends(setFriendsUser).build()); //обновить

            } else {
                throw new FriendException("У пользователя " + userId +
                        " нет в друзьях пользователя" + friendId);
            }


            if (setFriendsFriend.contains(userId)) {
                setFriendsFriend.remove(userId);//удалить из друзей
                up(friend.toBuilder().clearFriends().friends(setFriendsFriend).build()); //обновить
            } else {
                throw new FriendException("У пользователя " + friendId +
                        " нет в друзьях пользователя" + userId);
            }

        }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    public List<User> getMyFriends(Integer userId) {
        Util.valid(userId);

        return users.getUser(userId).getFriends().stream()
                .filter(uId -> users.isUser(uId))
                .map(uId -> users.getUser(uId))
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


    private User noName(User user, int generatedId) {
        String name = user.getName();

        if (name == null  || name.isBlank() || name.isEmpty()) {
            return user.toBuilder().id(generatedId).name(user.getLogin()).build();
        }

        return user.toBuilder().id(generatedId).build();
    }

}
