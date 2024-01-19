INSERT INTO TEST (WORD)
VALUES ('Привет');

DELETE FROM PUBLIC.USERS;
ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;
ALTER TABLE FRIENDS ALTER COLUMN FRIENDS_ID RESTART WITH 1;

DELETE FROM PUBLIC.FILMS;
ALTER TABLE PUBLIC.FILMS ALTER COLUMN FILM_ID RESTART WITH 1;
ALTER TABLE PUBLIC.FILM_LIKES ALTER COLUMN FILM_LIKES_ID RESTART WITH 1;

DELETE FROM PUBLIC.MPA;
ALTER TABLE PUBLIC.MPA ALTER COLUMN MPA_ID RESTART WITH 1;

DELETE FROM PUBLIC.FILM_GENRE;
ALTER TABLE PUBLIC.FILM_GENRE ALTER COLUMN GENRE_ID RESTART WITH 1;
ALTER TABLE PUBLIC.GENRES ALTER COLUMN GENRES_ID RESTART WITH 1;

--Удаляется каскадом
--DELETE FROM PUBLIC.FILM_LIKES
--DELETE FROM PUBLIC.FILM_LIKES;
--DELETE FROM PUBLIC.GENRES;

INSERT INTO MPA (MPA_ID, MPA_RATING)
VALUES (1,'G'),
(2,'PG'),
(3,'PG-13'),
(4,'R'),
(5,'NC-17');

INSERT INTO PUBLIC.FILM_GENRE (GENRE_ID, GENRE)
VALUES(1,'Комедия'),
(2,'Драма'),
(3,'Мультфильм'),
(4,'Триллер'),
(5,'Документальный'),
(6,'Боевик');

