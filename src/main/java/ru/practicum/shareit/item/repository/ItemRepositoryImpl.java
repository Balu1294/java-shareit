package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
    public ItemDto createItem(Integer ownerId,ItemDto itemDto, User user) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(idGenerator++);
        List<Item> itemsOfOwner = new ArrayList<>();
        itemsOfOwner.add(item);
        item.setOwner(user);
        items.put(item.getOwner().getId(), itemsOfOwner);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void updateItem(Integer ownerId, Integer itemId) {
//        items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getItemById(Integer ownerId, Integer itemId) {
        List<Item> items1 = items.get(ownerId);
        Item item = items1.stream().filter(i -> i.getId()==itemId).collect(Collectors.toList()).get(0);
        return Optional.of(item);
    }

    @Override
    public List<Item> getAllItems(Integer ownerId) {
        return new ArrayList<>(items.get(ownerId));
    }
}
