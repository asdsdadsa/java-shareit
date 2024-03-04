package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;
    private User userFirst;
    private User secondUser;
    private Item item;
    private Booking bookingFirst;
    private Booking bookingSecond;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {  // ПОРЯДОК СОЗДАНИЯ ОБЪЕКТОВ ОЧЕНЬ ЗДЕСЬ ВАЖЕН!

        userFirst = User.builder().id(1).name("John").email("john.doe@mail.com").build();

        secondUser = User.builder().id(2).name("NameTest").email("test@mail.ru").build();

        item = Item.builder().id(1).name("ItemTest").description("DescriptionTest").available(true).owner(userFirst).build();

        bookingFirst = Booking.builder().id(1).start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(1)).item(item).booker(userFirst).status(Status.APPROVED).build();


        bookingSecond = Booking.builder().id(2).start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(1)).item(item).booker(userFirst).status(Status.WAITING).build();

        bookingDto = BookingDto.builder()     // создаю сам так toBookingDto работает с BookingDtoFull
                .itemId(1).start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(1)).status(Status.APPROVED).build();

    }


    @Test
    void createBookingTest() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingFirst);

        BookingDtoFull bookingDtoFullCreated = bookingService.createBooking(bookingDto, anyInt());

        assertEquals(bookingFirst.getItem(), bookingDtoFullCreated.getItem());
        assertEquals(secondUser, bookingDtoFullCreated.getBooker());


        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingWrongOwnerTest() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userFirst));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, anyInt()));
    }

    @Test
    void createBookingItemBookedTest() {
        item.setAvailable(false);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(secondUser));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, anyInt()));
    }

    @Test
    void createBookingNotValidEndTest() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(secondUser));

        bookingDto.setEnd(LocalDateTime.of(2000, 9, 12, 0, 0));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, anyInt()));
    }

    @Test
    void approveBookingTest() {
        BookingDtoFull bookingDtoFullCreated;

        when(bookingRepository.findById((anyInt()))).thenReturn(Optional.ofNullable(bookingSecond));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingSecond);


        bookingDtoFullCreated = bookingService.approveBooking(userFirst.getId(), item.getId(), true);
        assertEquals(bookingDtoFullCreated.getStatus(), Status.APPROVED);

        bookingDtoFullCreated = bookingService.approveBooking(userFirst.getId(), item.getId(), false);
        assertEquals(bookingDtoFullCreated.getStatus(), Status.REJECTED);


        verify(bookingRepository, times(2)).save(any(Booking.class));

    }

    @Test
    void bookingByIdTest() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingFirst));

        BookingDtoFull bookingDtoFullCreated = bookingService.bookingById(userFirst.getId(), bookingFirst.getId());

        assertEquals(bookingFirst.getItem(), bookingDtoFullCreated.getItem());
        assertEquals(bookingFirst.getStatus(), bookingDtoFullCreated.getStatus());
        assertEquals(bookingFirst.getBooker(), bookingDtoFullCreated.getBooker());

        verify(bookingRepository, times(1)).findById(1);
    }

    @Test
    void bookingByIdNotFoundTest() {      // Тест на неверное условие
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingFirst));

        assertThrows(NotFoundException.class, () -> bookingService.bookingById(bookingFirst.getId(), 99));
    }


    @Test
    void bookingByUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userFirst));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any(PageRequest.class))).thenReturn(List.of(bookingFirst));

        String state = "ALL";

        List<BookingDtoFull> bookingOutDtoTest = bookingService.bookingByUser(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.bookingByUser(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "PAST";

        bookingOutDtoTest = bookingService.bookingByUser(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.bookingByUser(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "WAITING";

        bookingOutDtoTest = bookingService.bookingByUser(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.bookingByUser(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());
    }

    @Test
    void getAllBookingTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userFirst));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyInt(), any(PageRequest.class))).thenReturn(List.of(bookingFirst));

        String state = "ALL";

        List<BookingDtoFull> bookingOutDtoTest = bookingService.getAllBooking(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.getAllBooking(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "PAST";

        bookingOutDtoTest = bookingService.getAllBooking(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.getAllBooking(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "WAITING";

        bookingOutDtoTest = bookingService.getAllBooking(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(PageRequest.class))).thenReturn(List.of(bookingFirst));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.getAllBooking(userFirst.getId(), state, 5, 15);

        assertEquals(bookingOutDtoTest.get(0).getId(), bookingFirst.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), bookingFirst.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), bookingFirst.getBooker());
    }
}