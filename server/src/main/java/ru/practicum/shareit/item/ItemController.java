package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto itemCreate(@RequestHeader(HEADER_USER) Integer ownerId,
                              @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на создание вещи.");
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping(ITEM_ID_PATH)
    public ItemDto updateItem(@RequestHeader(HEADER_USER) Integer ownerId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable(ITEM_ID) Integer itemId) {
        log.info("Поступил запрос на обновление данных о вещи с id= {}", itemId);
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping(ITEM_ID_PATH)
    public ItemBookDto getItemById(@RequestHeader(HEADER_USER) Integer ownerId,
                                   @PathVariable(ITEM_ID) Integer itemId) {
        log.info("Поступил запрос на получение данных о вещи с id= {}", itemId);
        return itemService.getItemById(ownerId, itemId);
    }

    @GetMapping
    public List<ItemBookDto> getAllItemsByUser(@RequestHeader(HEADER_USER) Integer ownerId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на получение списка вещей пользователя с id= {}", ownerId);
        List<ItemBookDto> items = itemService.getItemsForUser(RequestItem.of(ownerId, from, size));
        return items;
    }

    @DeleteMapping(ITEM_ID_PATH)
    public ItemDto deleteItem(@PathVariable(ITEM_ID) Integer itemId,
                              @RequestHeader(HEADER_USER) Integer userId) {
        return itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(HEADER_USER) Integer ownerId,
                                    @RequestParam("text") String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        return itemService.search(RequestItem.of(ownerId, from, size, text));
    }

    @PostMapping("/{item-id}/comment")
    public CommentDto addComment(@RequestBody CommentDto comment,
                                 @RequestHeader(HEADER_USER) Integer userId,
                                 @PathVariable("item-id") Integer itemId) {
        return itemService.addComment(comment, userId, itemId);
    }
}
