package ru.practicum.shareit.booking.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.util.Status;

import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class BookingMapper {


    public static BookingDtoFull toBookingDto(Booking booking) {
        return BookingDtoFull.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

        if (bookingDto.getStatus() == null) {
            booking.setStatus(Status.WAITING);
        } else {
            booking.setStatus(bookingDto.getStatus());
        }
        return booking;
    }

    public static List<BookingDtoFull> toBookingDtoList(List<Booking> bookings) {
        List<BookingDtoFull> bookingList = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingList.add(toBookingDto(booking));
        }
        return bookingList;
    }
}
