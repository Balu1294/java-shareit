package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer ownerId, ItemDto itemDto);

    ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto);

    ItemBookDto getItemById(Integer ownerId, Integer itemId);

    List<ItemBookDto> getAllItems(Integer ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId);

    List<ItemDto> getItemsForUser(Integer userId);


}
