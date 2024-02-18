package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Integer bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(Integer ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, Status status, Pageable pageable);

    // ItemService
    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Integer itemId, Status status, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Integer itemId, Status status, LocalDateTime dateTime);

    List<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Integer itemId, Integer bookerId, Status status, LocalDateTime dateTime);
}
