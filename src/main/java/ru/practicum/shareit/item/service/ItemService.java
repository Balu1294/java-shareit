package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.RequestItem;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer ownerId, ItemDto itemDto);

    ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto);

    ItemBookDto getItemById(Integer ownerId, Integer itemId);

//    List<ItemBookDto> getAllItems(RequestItem requestItem);

    List<ItemDto> search(RequestItem requestItem);

    CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId);

    List<ItemBookDto> getItemsForUser(RequestItem requestItem);

    ItemDto deleteItem(Integer itemId, Integer userId);
}
