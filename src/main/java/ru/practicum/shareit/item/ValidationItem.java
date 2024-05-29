package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;

@UtilityClass
public class ValidationItem {
    public void validation(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new NotValidationItemException("Поле available не может быть пустым");
        }
        if (itemDto.getName().isBlank()) {
            throw new NotValidationItemException("Поле name не может быть пустым");
        }
        if (itemDto.getDescription() == null) {
            throw new NotValidationItemException("Поле description не может быть пустым");
        }
    }
}
