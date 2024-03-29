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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private Film film = Film.builder()
                .id(1)
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2000,10,1))
                .duration(100)
                .rate(0)
                .genres(new HashSet<>())
                .mpa(new Mpa(1,"G"))
                .build();

    private Film film1 = film.toBuilder().name("Измененное имя фильма").build();

    private Film film2 = Film.builder().name("Фильм1")
            .releaseDate(LocalDate.of(2001,10,1))
            .duration(30)
            .mpa(new Mpa(1,"G"))
            .genres(new HashSet<>())
            .build();

    @Test
    void createAndUpdateAndGetFilm() throws Exception {

        mockMvc.perform(post("/films").content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",Matchers.is(1)))
                .andExpect(jsonPath("$.name").value("Фильм"))
                .andExpect(jsonPath("$.releaseDate").value("2000-10-01"))
                .andExpect(jsonPath("$.duration").value("100"))
                .andExpect(jsonPath("$.description",Matchers.is("Описание")));

        mockMvc.perform(post("/films").content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        mockMvc.perform(put("/films").content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Измененное имя фильма"))
                .andExpect(jsonPath("$.releaseDate",
                        Matchers.is(LocalDate.of(2000,10,1).toString())))
                .andExpect(jsonPath("$.duration").value("100"))
                .andExpect(jsonPath("$.description").value("Описание"));

        mockMvc.perform(post("/films").content(objectMapper.writeValueAsString(film2))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[2].id").value("3"))
                .andExpect(jsonPath("$[0].name").value("Измененное имя фильма"))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-10-01"))
                .andExpect(jsonPath("$[0].duration").value("100"))
                .andExpect(jsonPath("$[0].description",Matchers.is("Описание")))
                .andExpect(jsonPath("$[2].name").value("Фильм1"))
                .andExpect(jsonPath("$[1].name").value("Фильм"));

    }

    @Test
    void exceptionTest() {

        assertEquals("Status expected:<404> but was:<200>", assertThrows(AssertionError.class, () ->
                        mockMvc.perform(put("/films").content(objectMapper.writeValueAsString(film))
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(result -> result
                                        .getResolvedException()
                                        .getClass()
                                        .equals(ValidationException.class))
                ).getMessage());

    }

    void validFilm(Film film) throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validTest() throws Exception {

        //проверка валидации поля name
        validFilm(film.toBuilder().name("").build());
        validFilm(film.toBuilder().name("    ").build());
        validFilm(film.toBuilder().name(null).build());

        //проверка валидации поля description
        validFilm(film.toBuilder().description("123456789_123456789_123456789_123456789_123456789_"
                        + "123456789_123456789_123456789_123456789_123456789_"
                        + "123456789_123456789_123456789_123456789_123456789_"
                        + "123456789_123456789_123456789_123456789_123456789_"
                        + "1")
                .build());

        //проверка валидации поля releaseDate
        validFilm(film.toBuilder().releaseDate(LocalDate.of(1894,1,1)).build());

        //проверка валидации поля duration
        validFilm(film.toBuilder().duration(-1).build());
    }
}