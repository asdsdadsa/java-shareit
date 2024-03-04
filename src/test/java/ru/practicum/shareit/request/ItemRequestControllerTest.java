package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("DescriptionTest")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.createRequest(any(ItemRequestDto.class), anyInt())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        verify(itemRequestService, times(1)).createRequest(itemRequestDto, 1);
    }

    @Test
    void requestByIdTest() throws Exception {
        when(itemRequestService.requestById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        verify(itemRequestService, times(1)).requestById(1, 1);
    }

    @Test
    void getRequestsTest() throws Exception {
        when(itemRequestService.getRequests(anyInt())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));

        verify(itemRequestService, times(1)).getRequests(1);
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));

        verify(itemRequestService, times(1)).getAllRequests(1, 0, 20);
    }

}