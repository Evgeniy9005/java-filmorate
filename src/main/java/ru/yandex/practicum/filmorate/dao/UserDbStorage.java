package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.exceptions.UserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component("users")
@RequiredArgsConstructor//создать конструктор, включить все финал поля
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int addUser(User user) {
               String sql = "INSERT INTO PUBLIC.USERS (" +
                "USER_EMAIL," +
                "USER_LOGIN," +
                "USER_NAME," +
                "USER_BIRTHDAY) " +
                "VALUES (?,?,?,?);";


        KeyHolder keyHolder = new GeneratedKeyHolder();

        int row;

        try {
            row = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql,new String[]{"USER_ID"});
                ps.setString(1,user.getEmail());
                ps.setString(2,user.getLogin());
                ps.setString(3,user.getName());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            },keyHolder);

        } catch (DataAccessException e) {
            throw new UserException("Сбой запроса при добавлении фильма " + user,e.getMessage());
        }

        log.info("Добавлено количество строк = {}, при добавлении пользователя \"{}\" ",row,user);

        return (int) keyHolder.getKey();
    }

    @Override
    public User addToFriends(int userId, int friendId) {
        //есть ли пользователи
        isUser(userId);
        isUser(friendId);

        //проверить, не в друзьях ли они уже
        if (isFriends(userId,friendId)) {
            throw new FriendException("Пользователи уже добавлены в друзья");
        }

        String sql = "INSERT INTO PUBLIC.FRIENDS (USER_ID, FRIEND_ID, CHECK_FRIEND) " +
                "VALUES(?, ?, ?);";

        int[] arrayType = new int[] {Types.INTEGER,Types.INTEGER,Types.BOOLEAN};

        Object[] args = new Object[]{userId, friendId, true};

        int row = jdbcTemplate.update(sql,args, arrayType);// userId добавил к себе в друзья friendId
        args[0] = friendId;
        args[1] = userId;
        args[2] = false; // friendId не подтверждал дружбу
        row +=  jdbcTemplate.update(sql,args, arrayType);// к friendId добавлен userId как подписчик

        log.info("Добавлено количество строк = {}," +
                " при добавлении пользователей  {} и {} в друзья ",row,userId,friendId);

        return getUser(friendId);//пользователь friendId сделал запрос в друзья к userId
    }



    @Override
    public boolean iAgreeFriend(int userId, int friendId) {

        //есть ли пользователи
        isUser(userId);
        isUser(friendId);

        //проверить что есть заявка в друзья
        if (!isFriends(userId,friendId)) {
            throw new FriendException("Пользователи уже добавлены в друзья");
        }

        String sql = String.format("SELECT FRIENDS_ID, USER_ID, FRIEND_ID, CHECK_FRIEND " +
                "FROM PUBLIC.FRIENDS " +
                "WHERE (USER_ID = %1$s AND FRIEND_ID = %2$s) OR (USER_ID = %2$s AND FRIEND_ID = %1$s)",userId,friendId);


        SqlRowSet friends = jdbcTemplate.queryForRowSet(sql);

        while (friends.next()) {
            if (!friends.getBoolean("CHECK_FRIEND")) {
                int recIduser = friends.getInt("USER_ID");
                int recIdfriend = friends.getInt("USER_ID");
                log.info("friends: USER_ID {}, FRIEND_ID {}, CHECK_FRIEND {}",
                        recIduser,
                        recIdfriend,
                        friends.getBoolean("CHECK_FRIEND")
                );

                //обновить значение подтверждения дружбы
                int row = jdbcTemplate.update("UPDATE PUBLIC.FRIENDS SET CHECK_FRIEND=true WHERE FRIENDS_ID=" +
                        friends.getInt("FRIENDS_ID"));

                log.info("Обновлено количество строк = {}",row);

                return true;
            }

        }

        return false;
    }

    @Override
    public void removeFromFriends(int userId, int friendId) {
        //есть ли пользователи
        isUser(userId);
        isUser(friendId);

        //проверить что пользователи в друзьях
        if (isFriends(userId,friendId)) {
            int row = jdbcTemplate.update("DELETE FROM PUBLIC.FRIENDS WHERE (USER_ID= ? AND FRIEND_ID = ?) " +
                    "OR (USER_ID= ? AND FRIEND_ID = ?);",userId,friendId,friendId,userId);
            log.info("Удалено количество строк = {}",row);
        } else {
            throw new FriendException("Пользователи не в друзьях");
        }


    }

    private boolean isFriends(int userId, int friendId) { //проверяет в друзьях пользователи или нет

        String sqlIsFriends = String.format("SELECT COUNT(FRIENDS_ID) AS count_f " +
                        "FROM PUBLIC.FRIENDS " +
                        "WHERE (USER_ID = %1$s AND FRIEND_ID = %2$s) OR (USER_ID = %2$s AND FRIEND_ID = %1$s)",
                userId,friendId);

        SqlRowSet countF = jdbcTemplate.queryForRowSet(sqlIsFriends);
        if (countF.next()) {
            int count = countF.getInt("count_f");
            log.info("countF: {}", count);
            if (count == 2) {
                return true;
            }
        }
        return false;
    }


    @Override
    public User removeUser(User user) {
        int userId = user.getId();

        isUser(userId); //проверить есть

        //запрос на удаление
        int row = jdbcTemplate.update("DELETE FROM PUBLIC.USERS WHERE USER_ID=" + userId);

        log.info("Удалено количество строк = {}",row);

        return user;
    }

    @Override
    public List<User> getUsers() {

        String sqlGetUsers = "SELECT USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "FROM PUBLIC.USERS;";

        List<User> users;

    try {
        users = jdbcTemplate.query(sqlGetUsers, (rs, rowNum) -> makeUser(rs));
    } catch (EmptyResultDataAccessException e) {
        throw new UserNotFoundException("Сбой при запросе списка пользователей!");
    }
        return users;
    }

    @Override
    public User getUser(User user) {
        return getUser(user.getId());
    }

    @Override
    public User getUser(int id) {

        String sqlGetUser = "SELECT USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "FROM PUBLIC.USERS WHERE USER_ID = ?;";

        User userWithoutFriends;
        try {
            userWithoutFriends = jdbcTemplate.queryForObject(sqlGetUser, (rs, rowNum) -> makeUser(rs),id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("Пользователь не найден id = " + id);
        }

        String sqlGetUserFriends = "SELECT FRIEND_ID FROM PUBLIC.FRIENDS " +
                "WHERE USER_ID = ? AND CHECK_FRIEND = TRUE;";

        List<Integer> listFriends = jdbcTemplate.queryForList(sqlGetUserFriends, Integer.class, id);

        User user = userWithoutFriends.toBuilder().clearFriends().friends(listFriends).build();

        log.info("Найден пользователь: {}", user);
        return user;
    }


     @Override
    public boolean isUser(int id) {

        SqlRowSet userId = jdbcTemplate.queryForRowSet("select user_id from users where user_id = " + id);
        if (userId.next()) {
            log.info("Найден пользователь: {}", userId.getInt("user_id"));
            return true;
        } else {
            throw new UserNotFoundException("Пользователь не найден id = " + id);
        }

    }


    public User updateUser(User user) {
        int userId = user.getId();

        //есть ли пользователи
        isUser(userId);

            int row = jdbcTemplate.update("UPDATE PUBLIC.USERS SET USER_EMAIL=?," +
                    " USER_LOGIN=?, " +
                    "USER_NAME=?, " +
                    "USER_BIRTHDAY=? " +
                    "WHERE USER_ID=?;",
                    user.getEmail(),user.getLogin(),user.getName(),user.getBirthday(),userId);

        //обновить друзей
        Set<Integer> userFriends = user.getFriends();
        if (userFriends == null || userFriends.isEmpty()) {

            row += jdbcTemplate.update("UPDATE PUBLIC.FRIENDS SET CHECK_FRIEND=FALSE WHERE FRIENDS_ID= " +
                    "(SELECT FRIENDS_ID FROM PUBLIC.FRIENDS WHERE USER_ID = ? AND CHECK_FRIEND = TRUE);",userId);
            return user.toBuilder().clearFriends().friends(new HashSet<>()).build();
        } else {
            userFriends.stream().map(fId -> {
                //добавить в друзья если нет
                User getUser = addToFriends(userId, fId);
                //подтвердить дружбу
                boolean status = iAgreeFriend(userId, fId);
                log.info("Статус подтверждения в друзья при обновлении  = {}," +
                        " пользователь добавлен в друзья {}", status, getUser.getId());
                return status;
            });
        }
            log.info("Обновлено количество строк = {}",row);

        return getUser(userId);
    }


    private User makeUser(ResultSet rs) throws SQLException {
        Integer userId = rs.getInt("user_id");
        String userEmail = rs.getString("user_email");
        String userLogin = rs.getString("user_login");
        String userName = rs.getString("user_name");
        LocalDate userBirthday = rs.getDate("user_birthday").toLocalDate();
        return User.builder()
                .id(userId)
                .email(userEmail)
                .login(userLogin)
                .name(userName)
                .birthday(userBirthday)
                .build();
    }

}
