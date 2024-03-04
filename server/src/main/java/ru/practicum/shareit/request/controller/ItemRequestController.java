package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }


    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(USER_HEADER) Integer userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping({"/{requestId}"})
    public ItemRequestDto requestById(@PathVariable Integer requestId, @RequestHeader(USER_HEADER) Integer userId) {
        return itemRequestService.requestById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(USER_HEADER) Integer userId) {
        return itemRequestService.getRequests(userId);
    }


    @GetMapping({"/all"})     // ВНИМАТЕЛЬНО СЛЕДИ ЧТОБЫ НЕ ПОСТАВИТЬ {} КОГДА НЕ НАДО
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_HEADER) Integer userId, @RequestParam(defaultValue = "0")
    Integer from, @RequestParam(defaultValue = "20") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }


}
