package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.contstant.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) int userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Поступил запрос на бронирование с состоянием {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_HEADER) int userId,
                                           @Valid @RequestBody BookingRequestDto requestDto) {
        log.info("Поступил запрос на создание бронирования {}, пользователем с id:={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER) int userId,
                                             @PathVariable(BOOKING_ID) Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) Integer userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookingsForOwner(userId, state, from, size);
    }

    @PatchMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> setApprove(@RequestHeader(USER_HEADER) Integer userId,
                                             @PathVariable(BOOKING_ID) Integer bookingId,
                                             @RequestParam Boolean approved) {
        return bookingClient.setApprove(userId, bookingId, approved);
    }

    @DeleteMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> deleteBooking(@PathVariable(BOOKING_ID) Integer bookingId,
                                                @RequestHeader(USER_HEADER) Integer userId) {
        return bookingClient.deleteBooking(bookingId, userId);
    }
}
