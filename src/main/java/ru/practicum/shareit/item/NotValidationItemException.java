package ru.practicum.shareit.item;

public class NotValidationItemException extends RuntimeException {
    public NotValidationItemException(String message) {
        super(message);
    }
}
