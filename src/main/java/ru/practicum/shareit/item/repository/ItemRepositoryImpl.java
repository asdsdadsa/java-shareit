package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class ItemRepositoryImpl implements ItemRepository {

    private UserService userService;

    private Map<Integer, Item> items = new HashMap<>();

    private Integer id = 1;

    @Autowired
    public ItemRepositoryImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Item createItem(Item item, Integer userId) {
        try {
            userService.userById(userId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        item.setId(id++);
        item.setOwner(userService.userById(userId));
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item patchItem(Item item, Integer itemId, Integer userId) { // для item update set unvaliable и item update set valiable 2 проверки
        if (userId == null) {  // 1 !!!
            throw new NotFoundException("Пользователь не может быть null");
        }

        Item itemPatch = items.get(itemId);

        if (!itemPatch.getOwner().getId().equals(userId)) {    // 2 !!!
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (item.getName() != null) {
            itemPatch.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemPatch.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemPatch.setAvailable(item.getAvailable());
        }
        items.put(itemId, itemPatch);
        return itemPatch;
    }


    @Override
    public Item itemById(Integer itemId) {     // !! Если используется в другом методе !! внимание
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException("Предмет с  id " + itemId + " не найден.");
        }
    }

    @Override
    public List<Item> getItems(Integer userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }


    @Override
    public List<Item> search(String text, Integer userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}