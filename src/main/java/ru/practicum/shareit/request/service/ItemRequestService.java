package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Integer userId);

    ItemRequestDto requestById(Integer requestId, Integer userId);

    List<ItemRequestDto> getRequests(Integer userId);

    List<ItemRequestDto> getAllRequests(Integer userId, Integer size, Integer from);

}
