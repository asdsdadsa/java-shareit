package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    Integer id;
    LocalDate start;
    LocalDate end;
    Item item;
    User booker;
    Status status;
}
