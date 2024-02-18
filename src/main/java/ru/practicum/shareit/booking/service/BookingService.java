package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;

import java.util.List;

public interface BookingService {
    BookingDtoFull createBooking(BookingDto bookingDto, Integer userId);

    BookingDtoFull approveBooking(Integer bookingId, Integer userId, Boolean approved);

    BookingDtoFull bookingById(Integer bookingId, Integer userId);

    List<BookingDtoFull> bookingByUser(Integer userId, String state);

    List<BookingDtoFull> getAllBooking(Integer userId, String state);
}
