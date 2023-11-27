package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotations.MinDate;

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
