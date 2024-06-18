package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                UserMapper.toUserDto(booking.getBooker()),
                UserMapper.toUserDto(booking.getBooker()).getId(),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus());
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        List<BookingDto> dtoList = bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
        return dtoList;
    }

}
