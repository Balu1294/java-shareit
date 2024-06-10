package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {
//    ItemDto createItem(Integer ownerId, ItemDto itemDto);
//
//    ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto);
//
//    ItemDto getItemById(Integer ownerId, Integer itemId);
//
//    List<Item> getAllItems(Integer ownerId);
//
//    List<ItemDto> search(String text);

}
