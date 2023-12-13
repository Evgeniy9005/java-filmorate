package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** имплементирующие новые интерфейсы,
 и перенесите туда всю логику хранения,
 обновления и поиска объектов.*/
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> userMap = new HashMap<>();

    @Override
    public void addUser(User user) {
        userMap.put(user.getId(), user);
        //  log.info("Добавлен пользователь= " + user);
    }


    @Override
    public User getUser(User user) {
        Integer id = user.getId();
        if(userMap.containsKey(id)) {
            return userMap.get(id);
        }
        throw new ValidationException("Пользователя не найден " + user);
    }

    @Override
    public User getUser(int userId) {

        if(userMap.containsKey(userId)) {
            return userMap.get(userId);
        }
        throw new ValidationException("Пользователя не найден под идентификатором " + userId);
    }

    @Override
    public User removeUser(User user) {
        Integer id = user.getId();
        if(userMap.containsKey(id)) {
            return userMap.remove(id);
        }
        throw new ValidationException("Пользователя не найден " + user);

    }

   /* @Override
    public void up(User user) {
        int idUser = user.getId();

        if (userMap.containsKey(idUser)) {
            userMap.remove(idUser);
            userMap.put(idUser,noName(user,idUser));
            //    log.info("Обновлен пользователь= " + user);
            return userMap.get(idUser);
        } else {
            throw new ValidationException("Токого пользователя нет " + user);
        }
    }*/

    @Override
    public List<User> getUsers() {
        //  log.info("Отправлен список пользователей, в количестве " + userMap.size());

        return new ArrayList<>(userMap.values());
    }



    /*private User noName(User user, int generatedId) {
        String name = user.getName();

        if (name == null  || name.isBlank() || name.isEmpty()) {
            return user.toBuilder().id(generatedId).name(user.getLogin()).build();
        }

        return user.toBuilder().id(generatedId).build();
    }*/
}
