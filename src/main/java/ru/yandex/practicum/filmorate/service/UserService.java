package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.exceptions.InputParametersException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**Создайте UserService, который будет отвечать за такие операции с пользователями,
 - как добавление в друзья,
 - удаление из друзей,
 - вывод списка общих друзей.
  Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
  То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.*/

@Slf4j
@Service
public class UserService {

    private UserStorage users;
    private static Integer globalId = 0;

    @Autowired
    public UserService(UserStorage users) {
        this.users = users;
    }

    private static Integer getNextId() {
        return ++globalId;
    }

    public User create(User user) {
        User newUser = noName(user,getNextId());
        users.addUser(newUser);
        UserService.log.info("В сервисе! Добавлен пользователь = "+newUser);
        return users.getUser(newUser);
    }

    public User up(User user) {
        int id = user.getId();

        users.removeUser(user);
        User upUser = noName(user,id);
        users.addUser(upUser);
        UserService.log.info("Обновлен пользователь= " + upUser);

        return users.getUser(upUser);
    }

    public List<User> getUsers() {
        return users.getUsers();
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public User addToFriends(Integer userId, Integer friendId) {
        valid(userId, friendId); // проверка входных параметров

        //создает и обновляет пользовател
        up(users.getUser(userId).toBuilder().friend(userId).build());

        //создает и обновляет пользовател возвращает  добавлинего пользователя в друзьядруга
        return up(users.getUser(userId).toBuilder().friend(friendId).build());
    }

    public void removeFromFriends(Integer userId, Integer friendId) { //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
        valid(userId, friendId); // проверка входных параметров
        User user = users.getUser(userId);

        if(user.isFriend(friendId)) { //мой друг?
            Set<Integer> setFriends = user.getFriends();
            setFriends.remove(friendId);//удалить из друзей
            up(user.toBuilder().friends(setFriends).build()); //обновить
        } else {
            throw new FriendException("Нет в друзьях пользователя под идентификаторм " + friendId);
        }

    }

    public List<User> getMyFriends(Integer userId) { //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.

        return List.of();
    }

    public void getMutualFriends(Integer id, Integer otherId) { //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.

    }

    private void valid(Integer id, Integer friendId) {
        if(id == null || id == 0) {
            throw new InputParametersException("Отсутствует идентификатор " + id);
        }

        if(friendId == null || friendId == 0) {
            throw new InputParametersException("Отсутствует идентификатор " + friendId);
        }

        if(id < 0 || friendId < 0) {
            throw new InputParametersException("Идентификатор не может быть отрицательным " + friendId);
        }
    }

    boolean valid(Integer id) {
        if(id == null || id == 0) {
            throw new InputParametersException("Отсутствует идентификатор " + id);
        }

        return false;
    }

    private User noName(User user, int generatedId) {
        String name = user.getName();

        if (name == null  || name.isBlank() || name.isEmpty()) {
            return user.toBuilder().id(generatedId).name(user.getLogin()).build();
        }

        return user.toBuilder().id(generatedId).build();
    }

}
