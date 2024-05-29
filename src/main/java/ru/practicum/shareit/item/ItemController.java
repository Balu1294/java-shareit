package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto itemCreate(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на создание вещи");
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{item-id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                              @Valid @RequestBody ItemDto itemDto,
                              @PathVariable("item-id") Integer itemId) {
        log.info("Поступил запрос на обновление данных о вещи с id= {}", itemId);
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{item-id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                               @PathVariable("item-id") Integer itemId) {
        log.info("Поступил запрос на получение данных о вещи с id= {}", itemId);
        return itemService.getItemById(ownerId, itemId);
    }

    @GetMapping
    public List<Item> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Поступил запрос на получение списка вещей пользователя с id= {}", ownerId);
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                    @RequestParam("text") String text) {
        return itemService.search(text);
    }
}
