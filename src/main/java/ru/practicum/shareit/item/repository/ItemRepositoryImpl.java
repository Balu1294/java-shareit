package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Integer, Item> items;
    private int idGenerator = 1;

    @Override
    public void createItem(Item item) {
        item.setId(idGenerator++);
        items.put(item.getId(), item);
    }

    @Override
    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getItemById(Integer id) {
        return Optional.of(items.get(id));
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
