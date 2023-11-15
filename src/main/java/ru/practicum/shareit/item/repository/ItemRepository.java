package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item, Integer userId);

    Item patchItem(Item user, Integer itemId, Integer userId);

    Item itemById(Integer itemId);

    List<Item> getItems(Integer userId);

    List<Item> search(String text, Integer userId);
}
