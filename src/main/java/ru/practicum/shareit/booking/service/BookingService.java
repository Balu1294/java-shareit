package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Integer userId);

    void removeBooking(Integer bookingId, Integer userId);

    BookingDto getBookingById(Integer bookingId, Integer userId);

    BookingDto setApprove(Integer bookingId, String approve, Integer userId);

    List<BookingDto> getBookingByUser(Integer userId, String state);

    List<BookingDto> getBookingForOwner(Integer userId, String state);

}
