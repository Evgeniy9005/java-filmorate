package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
public class User {
    @Builder.Default
    private int id=0;

    @NotNull
    @Email
    @NotBlank
    @NotEmpty
    private String email;

    @NotNull
    @NotBlank
    @NotEmpty
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    public User(int id, @NotNull String email, @NotNull String login, String name, @Past LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
