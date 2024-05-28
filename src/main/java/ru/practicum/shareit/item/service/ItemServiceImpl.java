package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.NotFoundItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Integer ownerId, ItemDto itemDto) {
        userRepository.getUserById(ownerId);
        User user = UserMapper.toUser(userRepository.getUserById(ownerId));
        return itemRepository.createItem(ownerId, itemDto, user);
    }

    @Override
    public ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto) {
        getItemById(ownerId, itemId);
        userRepository.getUserById(ownerId);
        return itemRepository.updateItem(ownerId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(Integer ownerId, Integer itemId) {
        return itemRepository.getItemById(ownerId, itemId);
    }

    @Override
    public List<Item> getAllItems(Integer ownerId) {
        return itemRepository.getAllItems(ownerId);
    }
}
