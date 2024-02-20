package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoFull createBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.createBooking(bookingDto, userId);        // @RequestHeader("X-Sharer-User-Id") для заголовок запроса
    }

    @PatchMapping({"{bookingId}"})  // ?state={state} не пишется
    public BookingDtoFull approveBooking(@PathVariable Integer bookingId, @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }                 // RequestParam для того что после ? то есть для доп инф /bookings/:bookingId?approved=true

    @GetMapping({"{bookingId}"})
    public BookingDtoFull bookingById(@PathVariable Integer bookingId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.bookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoFull> bookingByUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.bookingByUser(userId, state, from, size); //  required = false значит не обязателен
    }

    @GetMapping("/owner")        // !!!
    public List<BookingDtoFull> getAllBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "20") Integer size) {
        return bookingService.getAllBooking(userId, state, from, size);
    }
}
