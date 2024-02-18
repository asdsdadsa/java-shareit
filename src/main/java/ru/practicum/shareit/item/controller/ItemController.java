package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createItem(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.patchItem(itemDto, itemId, userId);
    }


    @GetMapping("/{itemId}")
    public ItemDto itemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.itemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "20") Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "20") Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Integer itemId, @RequestBody @Valid CommentDto commentDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}