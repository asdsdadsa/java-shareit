package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

// @UtilityClass генерирует приватный конструктор, который создаёт исключение, окончательно выводит класс и делает все методы статическими.
@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription()).available(item.getAvailable()).build();
        if (item.getRequest() != null) {    // ТЗ 15
            itemDto.setRequestId(item.getRequest().getId()); // ТЗ 15
        } // ТЗ 15
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder().id(itemDto.getId()).name(itemDto.getName()).description(itemDto.getDescription()).available(itemDto.getAvailable()).build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item : items) {
            itemDtoList.add(toItemDto(item));
        }
        return itemDtoList;
    }
}
