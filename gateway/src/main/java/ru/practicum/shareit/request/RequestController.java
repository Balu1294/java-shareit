package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.booking.BookingController.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody RequestDto itemRequest,
                                                 @RequestHeader(USER_HEADER) Integer userId) {
        return client.add(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsForUser(@RequestHeader(USER_HEADER) Integer userId) {
        return client.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsPageable(@RequestHeader(USER_HEADER) Integer userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                          @Positive @RequestParam(defaultValue = "10") int size) {
        return client.getItemRequestsPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(USER_HEADER) Integer userId,
                                                     @Positive @PathVariable Integer requestId) {
        return client.getItemRequest(userId, requestId);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> deleteItemRequest(@Positive @PathVariable("requestId") Integer requestId,
                                                    @RequestHeader(USER_HEADER) Integer userId) {
        return client.delete(requestId, userId);
    }
}
