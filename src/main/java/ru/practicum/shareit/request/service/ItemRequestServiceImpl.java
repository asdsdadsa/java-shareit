package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDtoList;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;

    private ItemRepository itemRepository;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }


    @Transactional
    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Integer userId) {

        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Description не может быть пустым.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestRepository.save(itemRequest);
        return toItemRequestDto(itemRequest);
    }

    @Transactional
    @Override
    public ItemRequestDto requestById(Integer requestId, Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с id " + requestId + " не существует"));
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        itemRequestDto.setItems(ItemMapper.toItemDtoList(items));

        return itemRequestDto;
    }

    @Transactional
    @Override
    public List<ItemRequestDto> getRequests(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);

            //  список вещей для каждого запроса
            List<Item> items = itemRepository.findByRequestId(itemRequest.getId());

            itemRequestDto.setItems(toItemDtoList(items));
            itemRequestDtoList.add(itemRequestDto);
        }
        return itemRequestDtoList;
    }

    @Transactional
    @Override
    public List<ItemRequestDto> getAllRequests(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));

        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, pageable);

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);

            // список вещей для каждого запроса
            List<Item> items = itemRepository.findByRequestId(itemRequest.getId());

            itemRequestDto.setItems(toItemDtoList(items));
            itemRequestDtoList.add(itemRequestDto);
        }
        return itemRequestDtoList;
    }
}
