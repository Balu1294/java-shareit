package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    void createItem(Item item);
    void updateItem(Item item);
    Item getItemById(Integer id);
    List<Item> getAllItems();
}
