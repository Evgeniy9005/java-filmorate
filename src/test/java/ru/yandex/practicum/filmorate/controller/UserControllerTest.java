package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;

        private User user = User.builder()
                .id(1)
                .login("Росомаха")
                .email("email@email.ru")
                .name("Логан")
                .birthday(LocalDate.of(2000,5,10))
                .build();

        private User user1 = user.toBuilder().name("").build();

        private User user2 = User
                .builder()
                .email("deadpool@email.ru")
                .name("Уэйд")
                .login("Deadpool")
                .birthday(LocalDate.of(1991,1,30))
                .build();

        @Test
        void createAndUpdateAndGetUser() throws Exception {

            mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(user))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", Matchers.is(1)))
                    .andExpect(jsonPath("$.login").value("Росомаха"))
                    .andExpect(jsonPath("$.email").value("email@email.ru"))
                    .andExpect(jsonPath("$.name").value("Логан"))
                    .andExpect(jsonPath("$.birthday").value("2000-05-10"));

            mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

            mockMvc.perform(put("/users").content(objectMapper.writeValueAsString(user1))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", Matchers.is(1)))
                    .andExpect(jsonPath("$.login").value("Росомаха"))
                    .andExpect(jsonPath("$.email").value("email@email.ru"))
                    .andExpect(jsonPath("$.name").value("Росомаха"))
                    .andExpect(jsonPath("$.birthday").value("2000-05-10"));

            mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(user2))
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("1"))
                    .andExpect(jsonPath("$[1].id").value("2"))
                    .andExpect(jsonPath("$[2].id").value("3"))
                    .andExpect(jsonPath("$[0].name").value("Росомаха"))
                    .andExpect(jsonPath("$[0].birthday").value("2000-05-10"))
                    .andExpect(jsonPath("$[0].login").value("Росомаха"))
                    .andExpect(jsonPath("$[2].name").value("Уэйд"))
                    .andExpect(jsonPath("$[2].email").value("deadpool@email.ru"))
                    .andExpect(jsonPath("$[1].name").value("Логан"));

        }

        @Test
        void exceptionTest() {
            assertEquals("Status expected:<404> but was:<200>"
                    , assertThrows(AssertionError.class, () ->
                            mockMvc.perform(put("/users").content(objectMapper.writeValueAsString(user))
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isNotFound())
                                    .andExpect(result -> result
                                            .getResolvedException()
                                            .getClass()
                                            .equals(ValidationException.class))
                    ).getMessage());

        }

    void validUser(User user) throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validTest() throws Exception {
        //проверка валидации поля email
        validUser(user.toBuilder().email("").build());
        validUser(user.toBuilder().email("    ").build());
        validUser(user.toBuilder().email("email").build());
        validUser(user.toBuilder().email(null).build());

        //проверка валидации поля lodin
        validUser(user.toBuilder().login("").build());
        validUser(user.toBuilder().login("    ").build());
        validUser(user.toBuilder().login(null).build());

        //проверка валидации поля birthday
        validUser(user.toBuilder().birthday(LocalDate.of(3000,1,1)).build());
        validUser(user.toBuilder().birthday(null).build());
    }
}