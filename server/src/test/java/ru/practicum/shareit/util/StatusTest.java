package ru.practicum.shareit.util;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StatusTest {

    @Test
    void getEnumTest() {
        Status statusWaiting = Status.valueOf("WAITING");
        assertEquals(statusWaiting, Status.WAITING);

        Status statusApproved = Status.valueOf("APPROVED");
        assertEquals(statusApproved, Status.APPROVED);

        Status statusRejected = Status.valueOf("REJECTED");
        assertEquals(statusRejected, Status.REJECTED);

        Status statusCanceled = Status.valueOf("CANCELED");
        assertEquals(statusCanceled, Status.CANCELED);
    }
}
