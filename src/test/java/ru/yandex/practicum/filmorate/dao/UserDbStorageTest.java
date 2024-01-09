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
   // private UserDbStorage users;

    //private JdbcConnectService connectDB = new JdbcConnectService();

  //  UserStorage users= new UserDbStorage(this.jdbcTemplate);

    //private JdbcTemplate jdbcTemplate = connectDB.getTemplate();

    //  private UserDbStorage userDbStorage;

    /*private User user1 = User.builder()
            .id(1)
            .login("login1")
            .email("email1")
            .name("name1")
            .birthday(LocalDate.of(1990, 1, 1))
            .friends(Set.of())
            .build();

    private User user2 = User.builder()
            .id(1)
            .login("login2")
            .email("email2")
            .name("name3")
            .birthday(LocalDate.of(1990, 1, 1))
            .friends(Set.of())
            .build();*/

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
                "Пользователь 2 подал заявку в друзья");
        assertEquals(3,users.addToFriends(1,3).getId(),
                "Пользователь 3 подал заявку в друзья");

        assertEquals("Пользователь не найден id = 9999"
                ,assertThrows(UserNotFoundException.class, ()->
                        users.addToFriends(9999,9998)
                ).getMessage());


        assertTrue(users.iAgreeFriend(1,2),"Пользователь 1 подтвердил заявку в друзья от пользователя 2");
        assertFalse(users.iAgreeFriend(1,2),"Пользователь 1 подтвердил заявку в друзья от пользователя 2 второй раз");
        assertTrue(users.iAgreeFriend(3,1),"Пользователь 3 подтвердил заявку в друзья от пользователя 1 порядок ввода параметров не важен");

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