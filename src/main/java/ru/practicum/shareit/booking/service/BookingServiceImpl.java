package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.exception.NotFoundBookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.exception.NotValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Integer userId) {
        checkUser(userId);
        itemRepository.findById(bookingDto.getItem().getId()).orElseThrow(() -> new NotFoundItemException(String.format("Вещи с id: %d не существует", bookingDto.getItem().getId())));
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto));
        log.info("Бронирование с id: {} создано", booking.getId());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public void removeBooking(Integer bookingId, Integer userId) {
        checkUser(userId);
        Booking booking = BookingMapper.toBooking(getBookingById(bookingId, userId));
        bookingRepository.delete(booking);
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundBookingException(String.format("Бронирования с id: %d  не существует", bookingId)));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto setApprove(Integer bookingId, String approve, Integer userId) {
        Booking booking = BookingMapper.toBooking(getBookingById(bookingId, userId));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new NotFoundBookingException(String.format("Бронирование с id: %d уже имеет статус APPROVED", bookingId));
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundItemException(String.format("Пользователь с id: %d  не является владельцем вещи с id: %d",
                    userId, booking.getItem().getId()));
        }
        if (approve.equals("true")) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Статус для бронирования с id: {} изменен", bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingByUser(Integer userId, String state) {
        LocalDateTime time = LocalDateTime.now();
        checkUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, time, time);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByEndDesc(userId, time);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByEndDesc(userId, time);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusOrderByEndDesc(userId, time, Status.valueOf(state));
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.valueOf(state));
                break;
            default:
                throw new NotValidationException("Unknown state: " + state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getBookingForOwner(Integer userId, String state) {
        LocalDateTime time = LocalDateTime.now();
        checkUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingByUserId(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndTimeDesc(userId,
                        time, time);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(userId, time);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(userId, time);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByEndDesc(userId, time,
                        Status.valueOf(state));
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByEndDesc(userId, Status.valueOf(state));
                break;
            default:
                throw new NotValidationException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDtoList(bookings);
    }


    private void checkUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(String.format("Пользователя с id: %d не существует", userId)));
    }
}
