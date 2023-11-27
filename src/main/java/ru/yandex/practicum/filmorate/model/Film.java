package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotations.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
//@EqualsAndHashCode(exclude = {"id"})
@Builder(toBuilder = true)
public class Film {
    @Builder.Default
    private int id=0;

    @NotNull
    @NotBlank
    private String  name;

    @Size(max=200)
    private String description;
    @MinDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
