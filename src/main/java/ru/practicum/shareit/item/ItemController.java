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
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

}
