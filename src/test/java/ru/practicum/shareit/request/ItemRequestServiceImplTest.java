package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@SpringBootTest
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private User user;
    private Item item;

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

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("DescriptionTest")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = toItemRequestDto(itemRequest);
    }

    @Test
    void createRequestTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto createdItemRequestDto = itemRequestService.createRequest(itemRequestDto, user.getId());

        assertEquals(itemRequest.getId(), createdItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), createdItemRequestDto.getDescription());
        assertEquals(itemRequest.getRequester(), createdItemRequestDto.getRequester());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));    // в этом тесте ошибка если itemRequestFirst сюда поставить
    }

    @Test
    void requestByIdTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDto createdItemRequestDto = itemRequestService.requestById(1, user.getId());

        assertEquals(itemRequest.getId(), createdItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), createdItemRequestDto.getDescription());
        assertEquals(itemRequest.getRequester(), createdItemRequestDto.getRequester());

        verify(itemRequestRepository, times(1)).findById(1);
    }

    @Test
    void getRequestsTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByRequestId(anyInt())).thenReturn(List.of(item));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getRequests(user.getId());

        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedDesc(anyInt());
    }

    @Test
    void getAllRequestsTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByRequestId(anyInt())).thenReturn(List.of(item));
        when(itemRequestRepository.findAllByRequesterIdNot(anyInt(), any(Pageable.class))).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllRequests(user.getId(), 5, 15);

        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoList.get(0).getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(anyInt(), any(PageRequest.class));
    }
}