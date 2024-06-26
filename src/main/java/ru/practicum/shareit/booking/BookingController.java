package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.HEADER_USER;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на создание бронирования от пользователя с id: {}.", userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @DeleteMapping("/{booking-id}")
    public void removeBooking(@PathVariable("booking-id") Integer bookingId,
                              @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на удаление бронирования с id: {}", bookingId);
        bookingService.removeBooking(bookingId, userId);
    }

    @GetMapping("/{booking-id}")
    public BookingDto getBookingById(@PathVariable("booking-id") Integer bookingId,
                                     @RequestHeader(HEADER_USER) Integer userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PatchMapping("/{booking-id}")
    public BookingDto setAproove(@PathVariable("booking-id") Integer bookingId,
                                 @RequestParam String approved,
                                 @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на подтверждение бронирования");
        return bookingService.setApprove(bookingId, approved, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingByUser(@RequestHeader(HEADER_USER) Integer userId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForOwner(@RequestHeader(HEADER_USER) Integer userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingForOwner(userId, state);
    }
}
