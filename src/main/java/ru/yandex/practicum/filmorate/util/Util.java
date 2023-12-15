package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.exceptions.InputParametersException;

public class Util {
    public static void valid(Integer id1, Integer id2) {
        if (id1 == null || id1 == 0) {
            throw new InputParametersException("Отсутствует идентификатор " + id1);
        }

        if (id2 == null || id2 == 0) {
            throw new InputParametersException("Отсутствует идентификатор " + id2);
        }

        if (id1 < 0 || id2 < 0) {
            throw new InputParametersException("Идентификатор не может быть отрицательным " + id2);
        }
    }

    public static void valid(Integer id) {
        if (id == null || id == 0) {
            throw new InputParametersException("Отсутствует идентификатор " + id);
        }

        if (id < 0) {
            throw new InputParametersException("Идентификатор не может быть отрицательным " + id);
        }

    }
}
