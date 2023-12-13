package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
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

    @Singular
    private final Set<Integer> likes;

    public void setLike(int idUser){
        likes.add(idUser);
    }

    public Integer getNumberLikes() {
        return likes.size();
    }

    public void deleteLike(int idUser){
        if(likes.contains(idUser)) {
            likes.remove(idUser);
        }
    }
}
