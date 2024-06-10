package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ValidationItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, List<Item>> items;
    private int idGenerator = 1;

    @Override
    public ItemDto createItem(Integer ownerId, ItemDto itemDto) {
        ValidationItem.validation(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(idGenerator++);
        List<Item> itemsOfOwner = new ArrayList<>();
        itemsOfOwner.add(item);
        item.setOwnerId(ownerId);
        items.put(item.getOwnerId(), itemsOfOwner);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto) {
        if (!getItemById(ownerId, itemId).getOwnerId().equals(ownerId)) {
            throw new NotFoundItemException(String.format("Пользователь с id: %d не создавал вещь с id: %d",
                    ownerId, itemId));
        }
        Item item = items.get(ownerId).stream().filter(it -> it.getId().equals(itemId)).findFirst().orElseThrow(() ->
                new NotFoundItemException(String.format("Вещи с id= %d не существует", itemId)));
        if (itemDto.getId() != null) {
            item.setId(itemDto.getId());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getOwnerId() != null) {
            item.setOwnerId(itemDto.getOwnerId());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Integer ownerId, Integer itemId) {
        List<Item> itemList = new ArrayList<>();
        for (List<Item> list : items.values()) {
            itemList.addAll(list);
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

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> searchItems = new ArrayList<>();
        for (List<Item> itemList : items.values()) {
            itemList.stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())
                            & item.getAvailable().equals(true))
                    .forEach(item -> searchItems.add(ItemMapper.toItemDto(item)));
        }
        return searchItems;
    }
}
