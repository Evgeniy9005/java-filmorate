package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;
import ru.yandex.practicum.filmorate.annotations.MinDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Data
@Builder(toBuilder = true)
public class Film {

    private final Integer id;

    @NotBlank
    private final String name;

    @Size(max = 200)
    private final String description;

    @MinDate
    private final LocalDate releaseDate;

    @Positive
    private final int duration;

    @Builder.Default
    private final Integer rate = 0;

    private final Set<Genre> genres;

    private final Mpa mpa;
}
