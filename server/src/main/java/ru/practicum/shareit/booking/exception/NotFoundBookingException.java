package ru.practicum.shareit.booking.exception;

public class NotFoundBookingException extends RuntimeException {
    public NotFoundBookingException(String message) {
        super(message);
    }
}
