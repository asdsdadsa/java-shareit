package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StateTest {

    @Test
    void getEnumTest() {

        State stateAll = State.valueOf("ALL");
        assertEquals(stateAll, State.ALL);

        State stateCurrent = State.valueOf("CURRENT");
        assertEquals(stateCurrent, State.CURRENT);

        State statePast = State.valueOf("PAST");
        assertEquals(statePast, State.PAST);

        State stateFuture = State.valueOf("FUTURE");
        assertEquals(stateFuture, State.FUTURE);

        State stateWaiting = State.valueOf("WAITING");
        assertEquals(stateWaiting, State.WAITING);

        State stateRejected = State.valueOf("REJECTED");
        assertEquals(stateRejected, State.REJECTED);
    }
}
