package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    User user;
    User user1;

    @BeforeEach
    void start() {
        user = new User(1,"email", "login", "name",
                LocalDate.of(1999, 3, 3), Set.of());

        user1 = User
                .builder()
                .id(1)
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1999, 3, 3))
                .build();
    }



    @Test
    void getId() {
        assertEquals(1,user.getId());
    }

    @Test
    void getEmail() {
        assertEquals("email",user.getEmail());
    }

    @Test
    void getLogin() {
        assertEquals("login",user.getLogin());
    }

    @Test
    void getName() {
        assertEquals("name",user.getName());
    }

    @Test
    void getBirthday() {
        assertEquals("1999-03-03",user.getBirthday().toString());
    }

    @Test
    void setId() {
       // user.setId(2);
        assertEquals(1,user.getId());
    }

    @Test
    void setEmail() {
        assertEquals("new email",user.toBuilder().email("new email").build().getEmail());
    }


    @Test
    void testEquals() {
        assertTrue(user.equals(user1));
    }

    @Test
    void testHashCode() {
        assertEquals(user.hashCode(),user1.hashCode());
    }

    @Test
    void testToString() {
        assertEquals(user.toString(),user1.toString());
    }

}