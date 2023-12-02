package ru.yandex.practicum.filmorate.model;

import lombok.*;
//import javax.validation.constraints.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class User {
    @Builder.Default
    private int id = 0;

    @Email
    @NotBlank
    @NotEmpty
    private String email;

    @NotBlank
    @NotEmpty
    private String login;

    private String name;

    @NotNull
    @Past
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
