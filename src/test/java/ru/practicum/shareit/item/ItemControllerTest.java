package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = itemDto.builder()     // какие показатели я тестирую через andExpect такие и требуются тут
                .id(1)
                .name("ItemTest")
                .description("DescriptionTest")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1)
                .text("CommentTest")
                .authorName("AuthorTest")
                .created(LocalDateTime.now())
                .build();
    }


    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).createItem(itemDto, 1);
    }

    @Test
    void patchItemTest() throws Exception {
        when(itemService.patchItem(any(ItemDto.class), anyInt(), anyInt())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))      // это передаем если есть @RequestBody
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).patchItem(itemDto, 1, 1);
    }

    @Test
    void itemByIdTest() throws Exception {
        when(itemService.itemById(anyInt(), anyInt())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).itemById(1, 1);
    }

    // ТАМ ГДЕ ДАННЫЕ НЕ ОТПРАВЛЯЮТСЯ не требуется. В основном при @RequestBody при post и patch но иногда требуется даже в get запросах
    //                         .characterEncoding(StandardCharsets.UTF_8)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .accept(MediaType.APPLICATION_JSON)


    @Test
    void getItemsUserTest() throws Exception {
        when(itemService.getItems(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).getItems(1, 0, 20);
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "textTest")  // Требуется следить что я передаю. Тут нужно передать параметры. Это @RequestParam
                        .param("from", "0")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).search("textTest", 0, 20);
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.createComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn((commentDto));

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        verify(itemService, times(1)).createComment(1, 1, commentDto);
    }
}