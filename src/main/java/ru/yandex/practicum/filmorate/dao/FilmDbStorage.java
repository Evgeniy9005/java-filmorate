package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("films")
@RequiredArgsConstructor//создать конструктор, включить все финал поля
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int addFilm(Film film) {
        String sqlFilm = "INSERT INTO PUBLIC.FILMS " +
                "(FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, MPA_ID) " +
                "VALUES(?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int row;

        try {
            row = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sqlFilm,new String[]{"FILM_ID"});
                ps.setString(1,film.getName());
                ps.setString(2,film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4,film.getDuration());
                ps.setInt(5,film.getMpa().getId());
                return ps;
            },keyHolder);


        } catch (DataAccessException e) {
            throw new FilmException("Сбой запроса при добавлении фильма " + film,e.getMessage());
        }

        log.info("Добавлено количество строк = {}, при добавлении фильма \"{}\"",row,film);

        int filmId = (int) keyHolder.getKey();

        //добавить жанры в БД
        upFilmGenres(film.getGenres(),filmId);

        return filmId;
    }



    @Override
    public List<Film> getFilms() {

        List<Integer> listFilms = jdbcTemplate.queryForList(
                "SELECT FILM_ID FROM PUBLIC.FILMS ORDER BY FILM_ID ASC;", Integer.class
        );

        return listFilms.stream().map(fId -> getFilm(fId)).collect(Collectors.toList());
    }

    @Override
    public Film getFilm(int filmId) {

        String sqlFilm = "SELECT FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, MPA_ID " +
                "FROM PUBLIC.FILMS WHERE FILM_ID=?;";

        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlFilm, (rs, rowNum) -> makeFilm(rs),filmId);
            film = film.toBuilder()
                    .rate(getLikesFilm(filmId)) //количество оценок
                    .mpa(getMPA(film.getMpa().getId())) //рейтинг
                    .genres(getFilmGenre(filmId)).build(); //жанры фильма

            log.info("Получен фильм из БД = {}",film);

        } catch (DataAccessException e) {
            throw new FilmException("Сбой при получении фильма из базы данных!",e.getMessage());
        }

        return film;
    }

    private void upFilmGenres(Set<Genre> genres, int filmId) throws DataAccessException {

        if (genres == null || genres.isEmpty()) {
            deleteFilmHaveGenre(filmId);
        } else {
            String sqlGenres = "INSERT INTO PUBLIC.GENRES (FILM_ID, GENRE_ID) VALUES(?, ?);";

            //проверить что есть фильм
            isFilm(filmId);

            //удалить отношения
            deleteFilmHaveGenre(filmId);

            //добавить новый список жанров
            genres.stream().filter(g -> isGenre(g.getId())) //проверить что такие жанры есть
                    .map(g -> jdbcTemplate.update(sqlGenres,
                                new Object[]{filmId,g.getId()},
                                new int[]{Types.INTEGER,Types.INTEGER})
                    ).forEach(row -> log.info("Добавлена строка = {}, отношения жанров к фильму",row));

        }

    }

    private void deleteFilmHaveGenre(int filmId) {

        //проверить что есть фильм
        isFilm(filmId);

        //удалить все строки отношения жанров к фильму
        int row = jdbcTemplate.update("DELETE FROM PUBLIC.GENRES WHERE FILM_ID=" + filmId);

        log.info("Удалено количество строк = {}, отношения жанров к фильму",row);
    }

    private void isFilm(int filmId) {

        SqlRowSet getFilmId = jdbcTemplate.queryForRowSet(
                "SELECT FILM_ID FROM PUBLIC.FILMS WHERE FILM_ID = " + filmId
        );

        if (!getFilmId.next()) {
            throw new FilmNotFoundException("Фильм не найден id = " + filmId);
        }

        log.info("Найден фильм: {}", getFilmId.getInt("FILM_ID"));

    }

    private boolean isGenre(int genreId) {

        SqlRowSet getGenreId = jdbcTemplate.queryForRowSet(
                "SELECT GENRE_ID FROM PUBLIC.FILM_GENRE WHERE GENRE_ID = " + genreId
        );

        if (getGenreId.next()) {
            log.info("Найден жанр: {}", getGenreId.getInt("GENRE_ID"));
            return true;
        }

        throw new GenreException("Жанр не найден id = " + genreId);
    }

    private boolean isFilmHaveGenre(int filmId, int genreId) {

        SqlRowSet row = jdbcTemplate.queryForRowSet(
                String.format("SELECT GENRES_ID, FILM_ID, GENRE_ID FROM PUBLIC.GENRES " +
                        "WHERE FILM_ID = %s AND GENRE_ID = %s", filmId, genreId)
        );

        if (row.next()) {
           // throw new GenreException("Жанр не найден id = " +genreId);
            log.info("Найден присвоенный жанр {} фильму {} ", genreId, filmId);
            return false;
        }

        log.info("Не найден присвоенный жанр {} фильму {} ", genreId, filmId);
        return true;
    }

    private Set<Genre> getFilmGenre(int filmId) {

        String sqlGenres = "SELECT GENRE_ID FROM PUBLIC.GENRES WHERE FILM_ID = ? ORDER BY GENRE_ID ASC;";

        Set<Genre> genres;
        try {
            genres = jdbcTemplate.queryForList(sqlGenres, Integer.class,filmId) //получить все жанры фильма из БД
                    .stream()
                    .map(gId -> getGenre(gId))
                    .collect(Collectors.toSet());

            log.info("Получен список {} жанров фильма {}, из БД ",genres,filmId);

        } catch (EmptyResultDataAccessException e) {
            throw new GenreException(String.format("Сбой запроса жанров фильма \"%s\", из базы данных!",filmId));
        }

        return genres;
    }

    @Override
    public Film updateFilm(Film film) {

        int filmId = film.getId();

        //Есть ли фильм?
        isFilm(filmId);

        try {
            int row = jdbcTemplate.update("UPDATE PUBLIC.FILMS SET FILM_NAME=?, " +
                            "FILM_DESCRIPTION=?, " +
                            "FILM_RELEASE_DATE=?, " +
                            "FILM_DURATION=?, " +
                            "MPA_ID=? " +
                            "WHERE FILM_ID=?;",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    filmId);

        //Обновить жанры!
        upFilmGenres(film.getGenres(),filmId);

        log.info("Обновлен фильм {}, добавлено строк {} ",filmId,row);

        } catch (DataAccessException e) {
            log.warn(e.getMessage());
            throw new FilmException("Сбой при обновлении фильма!", e.getMessage());
        }

        return getFilm(filmId);
    }

    @Override
    public Film removeFilm(Film film) {

        int filmId = film.getId();

        isFilm(filmId); //проверить есть

        //запрос на удаление
        int row = jdbcTemplate.update("DELETE FROM PUBLIC.FILMS WHERE FILM_ID=" + filmId);

        log.info("Удален фильм {}. Количество строк {}",film,row);

        return film;
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) { // Пользователь ставит лайк фильму.

        try {
        //Пользователь есть?
        SqlRowSet user = jdbcTemplate.queryForRowSet("SELECT USER_ID FROM PUBLIC.USERS WHERE USER_ID = " + userId);

        if (user.next()) {
            log.info("Найден пользователь: {}, для оценки фильма", user.getInt("USER_ID"));
        } else {
            throw new UserNotFoundException("Пользователь не найден, для оценки фильма: userId = " + userId);
        }

        //Фильм есть?
        isFilm(filmId);

        //Оценить фильм!
        String sqlLike = "INSERT INTO PUBLIC.FILM_LIKES (FILM_ID, USER_ID) VALUES(?, ?);";

            int row = jdbcTemplate.update(sqlLike, new Object[] {filmId,userId}, new int[] {Types.INTEGER,Types.INTEGER});

            log.info("Добавлено строк {}, при оценке фильма {} пользователем {}", row, filmId, userId);

        } catch (DataAccessException e) {
            throw new FilmException("Сбой при запросе оценки фильма!", e.getMessage());
        }

    }

    private int getLikesFilm(int filmId) {

        String sqlIsFriends = "SELECT COUNT(FILM_LIKES_ID) FROM PUBLIC.FILM_LIKES WHERE FILM_ID = ?;";

        Integer likes = jdbcTemplate.queryForObject(sqlIsFriends,Integer.class,filmId);

        log.info("Получено оценок {} фильма {}", likes, filmId);

        return likes;
    }

    @Override
    public int removeLike(int filmId, int userId) {

        //запрос на удаление
        int row = jdbcTemplate.update("DELETE FROM PUBLIC.FILM_LIKES WHERE FILM_ID=? AND USER_ID=?;",filmId,userId);

        log.info("Удаление оценки фильма {} пользователем {}. Удалено строк {}",filmId,userId,row);

        return row;
    }

   /* @Override
    public Set<Integer> getLikes(int filmId) {
        return null;
    }*/

    @Override
    public Genre addGenre(String genre) {

        String sql = "INSERT INTO PUBLIC.FILM_GENRE (GENRE) VALUES(?);";

       // int row = jdbcTemplate.update(sql,genre);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int row;

        try {
            row = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql,new String[]{"GENRE_ID"});
                ps.setString(1,genre);
                return ps;
            },keyHolder);
        } catch (DuplicateKeyException e) {
            throw new GenreException(
                    String.format("Сбой запроса при добавлении жанра! Название \"%s\" уже есть",genre)
            );
        }

        log.info("Добавлено количество строк = {}, при добавлении жанра \"{}\" фильма",row,genre);
        return getGenre((int)keyHolder.getKey());
    }

    @Override
    public Genre getGenre(int genreId) {

       String sql = "SELECT GENRE_ID, GENRE FROM PUBLIC.FILM_GENRE WHERE GENRE_ID=?;";

        Genre g;

        try {

            g = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs),genreId);

            log.info("Получен жанр из БД = {}",g);

        } catch (EmptyResultDataAccessException e) {
            throw new GenreException("Жанра фильма не найден! id=" + genreId);
        } catch (DataAccessException e) {
            throw new GenreException("Сбой запроса жанра фильма!");
        }

        return g;
    }

    public List<Genre> getGenres() {

        return jdbcTemplate.query(
                "SELECT GENRE_ID, GENRE FROM PUBLIC.FILM_GENRE ORDER BY GENRE_ID ASC;",
                (rs, rowNum) -> makeGenre(rs)
        );

    }

    @Override
    public Mpa addMPA(String mpa) {

        String sql = "INSERT INTO PUBLIC.MPA (MPA_RATING) VALUES(?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int row;

        try {
            row = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql,new String[]{"MPA_ID"});
                ps.setString(1,mpa);
                return ps;
            },keyHolder);
        } catch (DuplicateKeyException e) {
            throw new RatingException(
                    String.format("Сбой запроса при добавлении рейтинга! Название \"%s\" уже есть",mpa)
            );
        }

        log.info("Добавлено количество строк = {}, при добавлении рейтинга \"{}\" фильма",row,mpa);
        return getMPA((int)keyHolder.getKey());
    }


    @Override
    public Mpa getMPA(int mpaId) {

        String sql = "SELECT MPA_ID, MPA_RATING FROM PUBLIC.MPA WHERE MPA_ID=?;";

        Mpa mpa;

        try {

            mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMPA(rs),mpaId);

            log.info("Получен рейтинг из БД = {}",mpa);

        } catch (EmptyResultDataAccessException e) {
            throw new RatingException("Рейтинг фильма не найден! id=" + mpaId);
        } catch (DataAccessException e) {
            throw new RatingException("Сбой запроса рейтинга фильма!");
        }

        return mpa;
    }



    public List<Mpa> getAllMPA() {

        return jdbcTemplate.query(
                "SELECT MPA_ID, MPA_RATING FROM PUBLIC.MPA ORDER BY MPA_ID ASC;",
                (rs, rowNum) -> makeMPA(rs)
        );

    }

    private Genre makeGenre(ResultSet rs) throws SQLException {

        Integer genreId = rs.getInt("genre_id");

        String genre = rs.getString("genre");

        return new Genre(genreId,genre);

    }

    private Mpa makeMPA(ResultSet rs) throws SQLException {

        Integer mpaId = rs.getInt("mpa_id");

        String mpaRating = rs.getString("mpa_rating");

        return new Mpa(mpaId,mpaRating);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        Integer filmId = rs.getInt("FILM_ID");
        String filmName = rs.getString("FILM_NAME");
        String filmDescription = rs.getString("FILM_DESCRIPTION");
        LocalDate filmReleaseDate = rs.getDate("FILM_RELEASE_DATE").toLocalDate();
        Integer filmDuration = rs.getInt("FILM_DURATION");
        Integer mpaId = rs.getInt("MPA_ID");
        Mpa mpa = getMPA(mpaId);

        return Film.builder()
                .id(filmId)
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .mpa(mpa)
                .build();
    }

}
