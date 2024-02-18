package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.State;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.util.Status.*;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;


    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @Override
    public BookingDtoFull createBooking(BookingDto bookingDto, Integer userId) { // ПРОБЛЕМА БЫЛА ИЗ ЗА ДАТЫ И @RestControllerAdvice

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещи с id " + bookingDto.getItemId() + " не существует"));

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        Booking booking = toBooking(bookingDto);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(WAITING);

        if (item.getOwner().equals(user)) {
            throw new NotFoundException("Вещь с id " + bookingDto.getItemId() + " недоступна для бронирования для этого пользователя c id  " + userId + ".");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + bookingDto.getItemId() + " недоступна для бронирования.");
        }

        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Старт не может быть позже конца.");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Старт не может быть равен концу.");
        }

        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDtoFull approveBooking(Integer bookingId, Integer userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking с id " + bookingId + " не существует"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {    // !!!
            throw new NotFoundException("Только владелец может менять статус.");
        }

        if (approved) {     // true or false
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Неверный статус при обновлении.");
            }

            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }

        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Override
    public BookingDtoFull bookingById(Integer bookingId, Integer userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking с id " + bookingId + " не существует"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {                   // !!!
            throw new NotFoundException("Невозможно для пользователя с таким id " + userId + ".");
        } else {
            return toBookingDto(booking);
        }
    }

    @Transactional
    @Override
    public List<BookingDtoFull> bookingByUser(Integer userId, String state, Integer from, Integer size) {         // !!!

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        if (from < 0) { // ТЗ 15
            throw new ValidationException("Параметр пагинации не может быть отрицательным.");    // ТЗ 15
        }
        Pageable pageable = PageRequest.of(from / size, size);  // ТЗ 15       // ПОМОГЛО СДЕЛАЛ ТЕСТ С page

        List<Booking> bookings = null;

        State stateForCase = null;

        for (State value : State.values()) {
            if (value.name().equals(state)) {
                stateForCase = value;
            }
        }
        try {
            switch (stateForCase) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable);
                    break;
            }
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
        return toBookingDtoList(bookings);
    }

    @Transactional
    @Override
    public List<BookingDtoFull> getAllBooking(Integer userId, String state, Integer from, Integer size) {  // !!!

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        Pageable pageable = PageRequest.of(from, size);  // ТЗ 15

        List<Booking> bookings = null;

        State stateForCase = null;

        for (State value : State.values()) {
            if (value.name().equals(state)) {
                stateForCase = value;
            }
        }

        try {
            switch (stateForCase) {
                case ALL:
                    bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable);
                    break;
            }
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
        return toBookingDtoList(bookings);
    }
}
