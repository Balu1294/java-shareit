package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    ItemDto createItem(Integer ownerId, ItemDto itemDto);

    ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto);

    ItemDto getItemById(Integer ownerId, Integer itemId);

    List<Item> getAllItems(Integer ownerId);

    List<ItemDto> search(String text);

}
