package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.NotFoundItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public void createItem(Item item) {

    }

    @Override
    public void updateItem(Item item) {

    }

    @Override
    public Item getItemById(Integer id) {
        Item item = itemRepository.getItemById(id).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещь с id = %d отсутствует", id)));
        return item;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.getAllItems();
    }
}
