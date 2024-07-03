package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBooking;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Integer userId);

    BookingDto removeBooking(Integer bookingId, Integer userId);

    BookingDto getBookingById(Integer bookingId, Integer userId);

    BookingDto setApprove(Integer bookingId, Boolean approve, Integer userId);

    List<BookingDto> getBookingByUser(RequestBooking requestBooking);

    List<BookingDto> getBookingForOwner(RequestBooking requestBooking);
}
