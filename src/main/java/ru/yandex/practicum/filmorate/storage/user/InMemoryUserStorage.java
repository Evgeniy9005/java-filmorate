package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


/** имплементирующие новые интерфейсы,
 и перенесите туда всю логику хранения,
 обновления и поиска объектов.*/

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> userMap = new HashMap<>();

    private static Set<Integer> idUsers = new HashSet<>();

    @Override
    public int addUser(User user) {
        idUsers.add(user.getId());
        userMap.put(user.getId(), user);
        return user.getId();
    }

    public static boolean containsUser(int id) {
        return idUsers.contains(id);
    }

    @Override
    public boolean isUser(int id) {
        return userMap.containsKey(id);
    }

    @Override
    public boolean iAgreeFriend(int userId, int friendId) {
        return false;
    }

    @Override
    public User addToFriends(int userId, int friendId) {
        return null;
    }

    @Override
    public void removeFromFriends(int userId, int friendId) {

    }

    @Override
    public User updateUser(User user) {
        return null;
    }


    @Override
    public User getUser(User user) {
        Integer id = user.getId();

        if (userMap.containsKey(id)) {
            return userMap.get(id);
        }

        throw new UserNotFoundException("Не возможно вернуть пользователя! " + user);
    }


    @Override
    public User getUser(int userId) {

        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        }

        throw new UserNotFoundException("Не возможно вернуть пользователя под ID! " + userId);

    }


    @Override
    public User removeUser(User user) {
        Integer id = user.getId();

        if (userMap.containsKey(id)) {
            return userMap.remove(id);
        }

        throw new UserNotFoundException("Не возможно удалить пользователя! " + user);

    }


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }


}
