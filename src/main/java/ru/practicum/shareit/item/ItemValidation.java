package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemValidation {
    public void validItem(Item item) {
        if (item.getId() = null) {
            new NotFoundItemException("Такой вещи не существует");
        }
    }
}
