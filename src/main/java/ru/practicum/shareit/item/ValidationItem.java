package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotValidationException;

@UtilityClass
public class ValidationItem {
    public void validation(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new NotValidationException("Поле available не может быть пустым");
        }
        if (itemDto.getName().isBlank()) {
            throw new NotValidationException("Поле name не может быть пустым");
        }
        if (itemDto.getDescription() == null) {
            throw new NotValidationException("Поле description не может быть пустым");
        }
    }
}
