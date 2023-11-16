package ru.practicum.shareit.booking.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)     // по совету наставника для всех мап классов
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
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();
    }

    public static List<BookingDtoFull> toBookingDtoList(List<Booking> bookings) {
        List<BookingDtoFull> bookingList = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingList.add(toBookingDto(booking));
        }
        return bookingList;
    }
}
