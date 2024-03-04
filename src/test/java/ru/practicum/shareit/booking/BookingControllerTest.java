package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private BookingDto bookingDto;
    private User user;
    private Item item;
    private BookingDtoFull bookingDtoFull;


    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1)
                .name("John")
                .email("john.doe@mail.com")
                .build();


        item = Item.builder()
                .id(1)
                .name("ItemTest")
                .description("DescriptionTest")
                .available(true)
                .owner(user)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusMonths(9))
                .end(LocalDateTime.now().plusMonths(10))
                .build();

        bookingDtoFull = BookingDtoFull.builder()
                .id(1)
                .start(LocalDateTime.now().plusMonths(9))
                .end(LocalDateTime.now().plusMonths(10))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), anyInt())).thenReturn(bookingDtoFull);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoFull.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoFull.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoFull.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoFull.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).createBooking(bookingDto, 1);
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDtoFull);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")    // approved-параметры берем из метода запроса тут например из patch
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoFull.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoFull.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoFull.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoFull.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).approveBooking(1, 1, true);
    }

    @Test
    void bookingByIdTest() throws Exception {
        when(bookingService.bookingById(anyInt(), anyInt())).thenReturn(bookingDtoFull);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoFull.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoFull.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoFull.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoFull.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).bookingById(1, 1);
    }

    @Test
    void bookingByUserTest() throws Exception {
        when(bookingService.bookingByUser(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDtoFull));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "15")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoFull.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoFull.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoFull.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoFull.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).bookingByUser(1, "ALL", 5, 15);
    }

    @Test
    void getAllBookingTest() throws Exception {
        when(bookingService.getAllBooking(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDtoFull));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "15")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoFull.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoFull.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoFull.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoFull.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).getAllBooking(1, "ALL", 5, 15);
    }
}