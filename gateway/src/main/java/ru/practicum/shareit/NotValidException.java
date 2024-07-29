package ru.practicum.shareit;

public class NotValidException extends RuntimeException {
    public NotValidException(String message) {
        super(message);
    }
}
