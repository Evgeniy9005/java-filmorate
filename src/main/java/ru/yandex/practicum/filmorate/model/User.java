package ru.yandex.practicum.filmorate.model;

import lombok.*;
//import javax.validation.constraints.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {

    private final Integer id;

    @Email
    @NotBlank
    @NotEmpty
    private final String email;

    @NotBlank
    @NotEmpty
    private final String login;

    private String name;

    @Past
    private final LocalDate birthday;

    @Singular
    private final Set<Integer> friends;

    public boolean isFriend(int userId) {
        return friends.contains(userId);
    }
}
