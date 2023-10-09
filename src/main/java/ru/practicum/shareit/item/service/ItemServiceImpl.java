package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createItem(Item item, Integer userId) {
        log.info("Предмет создан, " + item);
        return itemRepository.createItem(item, userId);
    }

    @Override
    public Item patchItem(Item item, Integer itemId, Integer userId) {
        log.info("Предмет обновлен, " + item);
        return itemRepository.patchItem(item, itemId, userId);
    }

    @Override
    public Item itemById(Integer itemId) {
        log.info("Показан предмет с " + itemId + " id.");
        return itemRepository.itemById(itemId);
    }

    @Override
    public List<Item> getItems(Integer userId) {
        log.info("Показаны предметы " + itemRepository.getItems(userId) + ".");
        return itemRepository.getItems(userId);
    }

    public List<Item> search(String text, Integer userId) {
        log.info("Показаны предметы поиска " + itemRepository.search(text, userId) + ".");
        return itemRepository.search(text, userId);
    }
}
