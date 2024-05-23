package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    void createItem(Item item);
    void updateItem(Item item);
    Optional<Item> getItemById(Integer id);
    List<Item> getAllItems();
}
