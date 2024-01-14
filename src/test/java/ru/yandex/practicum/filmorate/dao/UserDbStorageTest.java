package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.FriendException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
//@SpringBootTest
//@Sql({"schema.sql", "data.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserDbStorageTest {



    private final JdbcTemplate jdbcTemplate;
    private UserStorage users;

    private User[] user = new User[6];

    @BeforeEach
    void start() {
        users = new UserDbStorage(this.jdbcTemplate);
        for(int i = 1; i < user.length+1; i++) {
            user[i-1] = User.builder()
                    .id(i)
                    .login("login"+i)
                    .email("email"+i)
                    .name("name"+i)
                    .birthday(LocalDate.of(1990, 1, 1))
                    .friends(Set.of())
                    .build();
            System.out.println(user[i-1]);
        }
    }


    @Test
    void addUser() {

        users.addUser(user[0]);
        users.addUser(user[1]);
        users.addUser(user[2]);
        users.addUser(user[3]);
        users.addUser(user[4]);
        users.addUser(user[5]);


        assertEquals(user[0],users.getUser(1),"найден пользователь 1");
        assertEquals(user[0],users.getUser(user[0]),"найден пользователь 1");
        assertEquals(user[5],users.getUser(user[5]),"найден пользователь 5");

        assertIterableEquals(List.of(user), users.getUsers(),"вернуть всех пользователей");

        users.removeUser(user[5]);
        assertEquals("Пользователь не найден id = 6"
                ,assertThrows(UserNotFoundException.class, ()->
                        users.getUser(user[5])
                ).getMessage());

        users.getUsers().stream().forEach(System.out::println);

        assertTrue(users.isUser(1),"есть пользователь 1?");
        assertTrue(users.isUser(2),"есть пользователь 2?");
        assertTrue(users.isUser(3),"есть пользователь 3?");

        assertEquals(2,users.addToFriends(1,2).getId(),
                "Пользователь 1 подал заявку в друзья к пользователю 2");
        assertEquals(3,users.addToFriends(1,3).getId(),
                "Пользователь 1 подал заявку в друзья к пользователю 3");
        assertEquals(4,users.addToFriends(1,4).getId(),
                "Пользователь 1 подал заявку в друзья к пользователю 4");

        assertEquals("Пользователь не найден id = 9999"
                ,assertThrows(UserNotFoundException.class, ()->
                        users.addToFriends(9999,9998)
                ).getMessage());


        assertTrue(users.iAgreeFriend(2,1),"Пользователь 2 подтвердил заявку в друзья от пользователя 1");
        assertFalse(users.iAgreeFriend(2,1),"Пользователь 2 подтвердил заявку в друзья от пользователя 1 второй раз");
        assertTrue(users.iAgreeFriend(3,1),"Пользователь 3 подтвердил заявку в друзья от пользователя 1 порядок ввода параметров не важен");


        assertIterableEquals(List.of(2,3,4), users.getUser(1).getFriends(),"у пользователя 1 должен быть друг 2 и 3");
        assertIterableEquals(List.of(1), users.getUser(2).getFriends(),"у пользователя 2 должен быть друг 1");
        assertIterableEquals(List.of(1), users.getUser(3).getFriends(),"у пользователя 3 должен быть друг 1");
        assertIterableEquals(List.of(), users.getUser(4).getFriends(),"у пользователя 4 не должно быть друзей");

        User userUp = users.getUser(1).toBuilder().login("loginUp")
                .email("emailUp")
                .name("nameUp")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        ;
        assertEquals(userUp,users.updateUser(userUp),"проверка обновления пользователя");


        users.removeFromFriends(1,2);
        assertEquals("Пользователи не в друзьях"
                ,assertThrows(FriendException.class, ()->
                        users.removeFromFriends(1,2) //проверка, что было произведено удаление из друзей
                ).getMessage());

    }

    @Test
    void removeUser() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void getUser() {
    }

    @Test
    void testGetUser() {
    }

    @Test
    void isUser() {

    }
}