package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    Film film;
    Film film1;

    @BeforeEach
    void start() {
    film = new Film(1,"Фильм",
            "Описание",
            LocalDate.of(2000,1,1),
            100,0, new HashSet<>(),new Mpa(1,"G"));

        film1 =  Film.builder()
                .id(1)
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(100)
                .rate(0)
                .genres(new HashSet<>())
                .mpa(new Mpa(1,"G"))
                .build();
    }

    @Test
    void getId() {
        assertEquals(1,film.getId());
    }

    @Test
    void getName() {
        assertEquals("Фильм",film.getName());
    }

    @Test
    void getDescription() {
        assertEquals("Описание",film.getDescription());
    }

    @Test
    void getReleaseDate() {
        assertEquals("2000-01-01",film.getReleaseDate().toString());
    }

    @Test
    void getDuration() {
        assertEquals(100,film.getDuration());
    }

    @Test
    void setId() {
        assertEquals(1,film.getId());
    }

    @Test
    void setName() {
        assertEquals("Новый фильм",film.toBuilder().name("Новый фильм").build().getName());
    }

    @Test
    void testEquals() {
    assertTrue(film.equals(film1));
    }

    @Test
    void testHashCode() {
    assertEquals(film.hashCode(),film1.hashCode());
    }

    @Test
    void testToString() {
        assertEquals(film.toString(),film1.toString());
    }
}