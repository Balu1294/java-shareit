package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.NotFoundItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, List<Item>> items;
    private int idGenerator = 1;

    @Override
    public ItemDto createItem(Integer ownerId, ItemDto itemDto, User user) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(idGenerator++);
        List<Item> itemsOfOwner = new ArrayList<>();
        itemsOfOwner.add(item);
        item.setOwner(user);
        items.put(item.getOwner().getId(), itemsOfOwner);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        if (item.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (item.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (item.getOwner() != null) {
            item.setOwner(User.builder().id(itemDto.getOwnerId()).build());
        }
        if (item.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Integer ownerId, Integer itemId) {
        List<Item> itemList = items.get(ownerId);
        if (itemList == null) {
            throw new NotFoundItemException("У пользователя с id=" + ownerId + " нет вещей");
        }
        Item item = itemList.stream()
                .filter(it -> it.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundItemException("Вещи с id = " + itemId + " не существует"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<Item> getAllItems(Integer ownerId) {
        return new ArrayList<>(items.get(ownerId));
    }
}
