package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto,
                                    @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на создание бронирования от пользователя с id: {}.", userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @DeleteMapping(BOOKING_ID_PATH)
    public BookingDto removeBooking(@PathVariable(BOOKING_ID) Integer bookingId,
                                    @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на удаление бронирования с id: {}", bookingId);
        return bookingService.removeBooking(bookingId, userId);
    }

    @GetMapping(BOOKING_ID_PATH)
    public BookingDto getBookingById(@PathVariable(BOOKING_ID) Integer bookingId,
                                     @RequestHeader(HEADER_USER) Integer userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PatchMapping(BOOKING_ID_PATH)
    public BookingDto setAproove(@PathVariable(BOOKING_ID) Integer bookingId,
                                 @RequestParam Boolean approved,
                                 @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на подтверждение бронирования");
        return bookingService.setApprove(bookingId, approved, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingByUser(@RequestHeader(HEADER_USER) Integer userId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingByUser(new RequestBooking(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForOwner(@RequestHeader(HEADER_USER) Integer userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingForOwner(new RequestBooking(userId, state, from, size));
    }
}
