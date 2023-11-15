package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    private ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        Item item = itemService.createItem(itemMapper.toItem(itemDto), userId);
        return itemMapper.toItemDto(item);
    }


    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        Item item = itemService.patchItem(itemMapper.toItem(itemDto), itemId, userId);
        return itemMapper.toItemDto(item);
    }


    @GetMapping("/{itemId}")
    public ItemDto itemByUserId(@PathVariable Integer itemId) {
        return itemMapper.toItemDto(itemService.itemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getItemsUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemService.getItems(userId)) {
            itemDtoList.add(itemMapper.toItemDto(item));
        }
        return itemDtoList;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemService.search(text, userId)) {
            itemDtoList.add(itemMapper.toItemDto(item));
        }
        return itemDtoList;
    }
}