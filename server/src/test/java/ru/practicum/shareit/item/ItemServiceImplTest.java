package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private ItemRequest itemRequest;
    private Booking booking;
    private Comment comment;
    private CommentDto commentDto;


    @BeforeEach
    void setUp() {
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
                .request(itemRequest)
                .build();

        itemDto = toItemDto(item);

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("DescriptionTest")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .booker(user)
                .item(item)
                .status(Status.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1)
                .text("TextTest")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = toCommentDto(comment);
    }


    @Test
    void createItemTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItemDto = itemService.createItem(itemDto, user.getId());

        assertEquals(item.getId(), createdItemDto.getId());
        assertEquals(item.getName(), createdItemDto.getName());
        assertEquals(item.getDescription(), createdItemDto.getDescription());
        assertEquals(item.getAvailable(), createdItemDto.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void patchItemTest() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemDto.setName("ItemTestUpdate");

        ItemDto updatedItemDto = itemService.patchItem(itemDto, item.getId(), user.getId());

        assertEquals(item.getId(), updatedItemDto.getId());
        assertEquals(item.getName(), updatedItemDto.getName());
        assertEquals(item.getDescription(), updatedItemDto.getDescription());
        assertEquals(item.getAvailable(), updatedItemDto.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void itemByIdTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(anyInt(), any(Status.class),
                any(LocalDateTime.class))).thenReturn(Optional.ofNullable(booking)); // если ставишь any то есть неопределенный у одного показателя другие тоже должны быть any
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyInt(), any(Status.class),
                any(LocalDateTime.class))).thenReturn(Optional.ofNullable(booking));

        ItemDto createdItemDto = itemService.itemById(user.getId(), item.getId());

        assertEquals(item.getId(), createdItemDto.getId());
        assertEquals(item.getName(), createdItemDto.getName());
        assertEquals(item.getDescription(), createdItemDto.getDescription());
        assertEquals(item.getAvailable(), createdItemDto.getAvailable());

        verify(itemRepository, times(1)).findById(anyInt());
    }

    @Test
    void getItemsTest() {
        when(itemRepository.findByOwnerIdOrderById(anyInt(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(anyInt(), any(Status.class),
                any(LocalDateTime.class))).thenReturn(Optional.ofNullable(booking)); // если ставишь any то есть неопределенный у одного показателя другие тоже должны быть any
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyInt(), any(Status.class),
                any(LocalDateTime.class))).thenReturn(Optional.ofNullable(booking));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        List<ItemDto> itemDtoList = itemService.getItems(user.getId(), 5, 15);

        assertEquals(item.getId(), itemDtoList.get(0).getId());
        assertEquals(item.getName(), itemDtoList.get(0).getName());
        assertEquals(item.getDescription(), itemDtoList.get(0).getDescription());
        assertEquals(item.getAvailable(), itemDtoList.get(0).getAvailable());

        verify(itemRepository, times(1)).findByOwnerIdOrderById(anyInt(), any(Pageable.class));
    }

    @Test
    void createCommentTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyInt(), anyInt(), any(Status.class),
                any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto createdCommentDto = itemService.createComment(item.getId(), user.getId(), commentDto);

        assertEquals(comment.getId(), createdCommentDto.getId());
        assertEquals(comment.getText(), createdCommentDto.getText());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void searchItemTest() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> searchedItems = itemService.search("qwe", 5, 15);

        assertEquals(item.getId(), searchedItems.get(0).getId());
        assertEquals(item.getName(), searchedItems.get(0).getName());
        assertEquals(item.getDescription(), searchedItems.get(0).getDescription());
        assertEquals(item.getAvailable(), searchedItems.get(0).getAvailable());

        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    void searchItemEmptyText() { // Тест на неверное условие
        List<ItemDto> itemDtoList = itemService.search("", 5, 15);

        assertTrue(itemDtoList.isEmpty());

        verify(itemRepository, times(0)).search(anyString(), any(Pageable.class));
    }

}