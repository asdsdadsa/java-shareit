package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDtoFull> json;

    LocalDateTime start = LocalDateTime.of(2024, 2, 17, 16, 0);
    LocalDateTime end = LocalDateTime.of(2024, 2, 17, 20, 0);


    @Test
    void testBookingDto() throws Exception {

        User user = User.builder()
                .id(1)
                .name("John")
                .email("john.doe@mail.com")
                .build();


        Item item = Item.builder()
                .id(1)
                .name("ItemTest")
                .description("DescriptionTest")
                .available(true)
                .owner(user)
                .build();

        BookingDtoFull bookingDtoFull = BookingDtoFull.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();


        JsonContent<BookingDtoFull> result = json.write(bookingDtoFull);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john.doe@mail.com");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("ItemTest");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("DescriptionTest");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }
}
