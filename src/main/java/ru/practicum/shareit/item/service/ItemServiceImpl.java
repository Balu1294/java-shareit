package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Integer ownerId, ItemDto itemDto) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователя с id: {}  не существует", ownerId)));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto) {
        getItemById(ownerId, itemId);
        userRepository.findById(ownerId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Integer ownerId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещи с id: {} не существует", itemId)));
        getItemById(ownerId, itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<Item> getAllItems(Integer ownerId) {
        return itemRepository.getAllItems(ownerId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text);
    }
}
