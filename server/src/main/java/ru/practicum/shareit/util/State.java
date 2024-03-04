package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.BadRequestException;

public enum State {

    ALL,

    CURRENT,

    PAST,

    FUTURE,

    WAITING,

    REJECTED;

    public static State getEnumValue(String state) {

        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
