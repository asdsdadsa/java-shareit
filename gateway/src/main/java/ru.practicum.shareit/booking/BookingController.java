package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.util.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.USER_HEADER;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDto bookingDto, @RequestHeader(USER_HEADER) Integer userId) {
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping({"/{bookingId}"})
    public ResponseEntity<Object> approveBooking(@PathVariable Integer bookingId, @RequestHeader(USER_HEADER) Integer userId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping({"/{bookingId}"})
    public ResponseEntity<Object> bookingById(@PathVariable Integer bookingId, @RequestHeader(USER_HEADER) Integer userId) {
        return bookingClient.bookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> bookingByUser(@RequestHeader(USER_HEADER) Integer userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "20") Integer size) {
        State stateBooking = State.from(state)
                .orElseThrow(() -> new InternalServerErrorException("Unknown state: " + state));
        return bookingClient.bookingByUser(userId, stateBooking, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBooking(@RequestHeader(USER_HEADER) Integer userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "20") Integer size) {
        State stateBooking = State.from(state)
                .orElseThrow(() -> new InternalServerErrorException("Unknown state: " + state));
        return bookingClient.getAllBooking(userId, stateBooking, from, size);
    }
}
