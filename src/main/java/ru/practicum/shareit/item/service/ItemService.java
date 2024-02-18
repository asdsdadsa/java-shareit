package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto patchItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemDto itemById(Integer userId, Integer itemId);

    List<ItemDto> getItems(Integer userId);

    List<ItemDto> search(String text);

    CommentDto createComment(Integer itemId, Integer userId, CommentDto commentDto);
}
