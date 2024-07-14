package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.NotValidException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.booking.BookingController.USER_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient client;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") Integer itemId,
                                              @RequestHeader(USER_HEADER) Integer userId) {
        return client.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsForUser(@RequestHeader(USER_HEADER) Integer userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return client.getItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Integer itemId,
                                             @RequestBody ItemDto item,
                                             @RequestHeader(USER_HEADER) Integer userId) {
        checkValidItemForUpdate(item);
        return client.update(userId, item, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto item,
                                          @RequestHeader(USER_HEADER) Integer userId) {
        return client.add(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Integer itemId,
                                             @RequestHeader(USER_HEADER) Integer userId) {
        return client.delete(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text,
                                         @RequestHeader(USER_HEADER) Integer userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        return client.search(userId, from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto comment,
                                             @RequestHeader(USER_HEADER) Integer userId,
                                             @PathVariable("itemId") Integer itemId) {
        return client.addComment(userId, itemId, comment);
    }

    private void checkValidItemForUpdate(ItemDto item) {
        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new NotValidException(String.format("Вещь с id запроса: %d не прошла валидацию. У вещи отсутствует название",
                        item.getRequestId()));
            }
        }
    }
}
