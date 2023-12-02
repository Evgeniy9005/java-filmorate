package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    Film film;
    Film film1;
    @BeforeEach
    void start() {
        film = new Film(1,"Фильм", "Описание",
                LocalDate.of(2000,1,1),100);

        film1 = Film.builder()
                .id(1)
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(100)
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
        film.setId(2);
        assertEquals(2,film.getId());
    }

    @Test
    void setName() {
        film.setName("Новый фильм");
        assertEquals("Новый фильм",film.getName());
    }

    @Test
    void setDescription() {
        film.setDescription("Новое описание");
        assertEquals("Новое описание",film.getDescription());
    }

    @Test
    void setReleaseDate() {
        film.setReleaseDate(LocalDate.of(2002,2,2));
        assertEquals("2002-02-02",film.getReleaseDate().toString());
    }

    @Test
    void setDuration() {
        film.setDuration(111);
        assertEquals(111,film.getDuration());
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