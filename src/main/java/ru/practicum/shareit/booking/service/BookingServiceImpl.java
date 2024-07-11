package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.exception.NotFoundBookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.exception.NotValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Integer userId) {
        User user = checkUser(userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещи с id: %d не существует.", bookingDto.getItemId())));
        if (item.getOwner() != null) {
            if (item.getOwner().getId().equals(userId)) {
                throw new NotFoundItemException("Пользователь выложивший вещь не может ее забронировать");
            }
        }

        if (item.getAvailable().equals(false)) {
            throw new NotValidationException(String.format("Вещь с id: %d недоступна для бронирования", item.getId()));
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new NotValidationException("Некорректное время старта или окончания");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        log.info("Бронирование с id: {} создано", booking.getId());
        return toBookingDto(booking);
    }

    @Override
    public BookingDto removeBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundBookingException(""));
        if (!userId.equals(booking.getBooker().getId())) {
            throw new NotFoundUserException(String.format("Пользователь с id:%d не является владельцем бронирования с id: %d",
                    userId, bookingId));
        }
        bookingRepository.delete(booking);
        log.info("Бронирование с Id: {} удалено", bookingId);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundBookingException(String.format("Бронирования с id: %d  не существует", bookingId)));
        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            throw new NotFoundUserException(String.format("Пользователь с id: %d не имеет доступа к бронированию с id: %d",
                    userId, bookingId));
        }
        log.info("Бронирование получено");
        return toBookingDto(booking);
    }

    @Override
    public BookingDto setApprove(Integer bookingId, Boolean approve, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundBookingException(String.format("Бронирования с id: %d  не существует", bookingId)));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new NotValidationException(String.format("Бронирование с id: %d уже имеет статус APPROVED", bookingId));
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundItemException(String.format("Пользователь с id: %d  не является владельцем вещи с id: %d",
                    userId, booking.getItem().getId()));
        }
        if (approve.equals(true)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Статус для бронирования с id: {} изменен.", bookingId);
        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingByUser(RequestBooking requestBooking) {
        Integer userId = requestBooking.getUserId();
        String state = requestBooking.getState().toUpperCase();
        validBooking(requestBooking);
        PageRequest pageRequest = PageRequest.of(requestBooking.getFrom() / requestBooking.getSize(),
                requestBooking.getSize());

        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        checkUser(userId);
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, time,
                        time, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByEndDesc(userId, time, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByEndDesc(userId, time, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusOrderByEndDesc(userId, time,
                        Status.valueOf(state), pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.valueOf(state),
                        pageRequest);
                break;
            default:
                throw new NotValidationException("Unknown state: " + state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getBookingForOwner(RequestBooking requestBooking) {
        Integer userId = requestBooking.getUserId();
        String state = requestBooking.getState().toUpperCase();
        validBooking(requestBooking);
        PageRequest pageRequest = PageRequest.of(requestBooking.getFrom() / requestBooking.getSize(),
                requestBooking.getSize());
        LocalDateTime time = LocalDateTime.now();
        checkUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingByUserId(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId,
                        time, time, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(userId, time, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(userId, time, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByEndDesc(userId, time,
                        Status.valueOf(state), pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByEndDesc(userId, Status.valueOf(state),
                        pageRequest);
                break;
            default:
                throw new NotValidationException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDtoList(bookings);
    }

    private void validBooking(RequestBooking requestBooking) {
        if (requestBooking.getFrom() < 0) {
            throw new NotValidationException(String.format("Запрос на бронирование от пользователя с id: %d не прошло валидацию." +
                    "Поле from: %d недействительно", requestBooking.getUserId(), requestBooking.getFrom()));
        }
        if (requestBooking.getSize() < 0) {
            throw new NotValidationException(String.format("Запрос на бронирование от пользователя с id: %d не прошло валидацию." +
                    "Поле size: %d недействительно", requestBooking.getUserId(), requestBooking.getSize()));
        }
    }

    private User checkUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователя с id: %d не существует", userId)));
    }
}
