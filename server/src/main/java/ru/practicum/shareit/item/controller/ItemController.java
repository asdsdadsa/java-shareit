package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_HEADER;


@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @PostMapping
    public ItemDto createItem(/*@Valid*/ @RequestBody ItemDto itemDto, @RequestHeader(USER_HEADER) Integer userId) {
        return itemService.createItem(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId, @RequestHeader(USER_HEADER) Integer userId) {
        return itemService.patchItem(itemDto, itemId, userId);
    }


    @GetMapping("/{itemId}")
    public ItemDto itemById(@RequestHeader(USER_HEADER) Integer userId, @PathVariable Integer itemId) {
        return itemService.itemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsUser(@RequestHeader(USER_HEADER) Integer userId, @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "20") Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "20") Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Integer itemId, @RequestBody /*@Valid */CommentDto commentDto, @RequestHeader(USER_HEADER) Integer userId) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}