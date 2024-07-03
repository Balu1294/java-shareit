package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemControllerTest {

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    ItemBookDto itemBookDto;
    ItemDto itemDto;
    UserDto userDto;
    int userId;
    int itemId;

    @BeforeEach
    public void fillItems() {
        userId = 1;
        itemId = 1;
        userDto = new UserDto(userId, "Jon Bon", "mail@mail.ru");
        itemBookDto = new ItemBookDto(itemId, "Отвертка", userDto, "Классная отвертка", true);
        itemDto = new ItemDto(itemId, "Стремянка", "Высокая стремянка", true,  userDto.getId(),1);
    }

    @Test
    public void getItemByIdWhenMethodInvokedReturnItem() {
        when(itemService.getItemById(itemId, userId)).thenReturn(itemBookDto);

        assertEquals(itemBookDto, itemController.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemNotFoundThrowException() {
        when(itemService.getItemById(itemId, userId)).thenThrow(NotFoundItemException.class);

        assertThrows(NotFoundItemException.class, () -> itemController.getItemById(itemId, userId));
    }

    @Test
    public void getItemsForUserWhenFoundOneItemReturnListWithOneItem() {
        List<ItemBookDto> items = List.of(itemBookDto);
        RequestItem requestItem = RequestItem.of(userId, 0, 10);

        when(itemService.getItemsForUser(requestItem)).thenReturn(items);

        List<ItemBookDto> foundItems = itemController.getAllItemsByUser(userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    public void getItemsForUserWhenItemNotFoundReturnEmptyList() {
        List<ItemBookDto> items = List.of();
        RequestItem requestItem = RequestItem.of(userId, 0, 10);

        when(itemService.getItemsForUser(requestItem)).thenReturn(items);

        List<ItemBookDto> foundItems = itemController.getAllItemsByUser(userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void updateItemWhenInvokedMethodReturnUpdatedItem() {
        ItemDto newItem = new ItemDto(1, "Updated Стремянка", "Updated Высокая стремянка",false, userDto.getId(),2);

        when(itemService.updateItem(userId, itemId, newItem)).thenReturn(newItem);

        ItemDto updatedItem = itemController.updateItem(itemId, newItem, userId);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getId(), updatedItem.getId());
        assertEquals(newItem.getName(), updatedItem.getName());
        assertEquals(newItem.getOwnerId(), updatedItem.getOwnerId());
        assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
        assertEquals(newItem.getDescription(), updatedItem.getDescription());
        assertEquals(newItem.getRequestId(), updatedItem.getRequestId());
    }

    @Test
    public void updateItemWhenItemNotFoundThrowException() {
        ItemDto newItem = new ItemDto(1, "Updated Стремянка", "Updated Высокая стремянка", false,userDto.getId(), 2);

        when(itemService.updateItem(userId, itemId, newItem)).thenThrow(NotFoundItemException.class);

        assertThrows(NotFoundItemException.class, () -> itemController.updateItem(itemId, newItem, userId));
    }

    @Test
    public void addItemWhenMethodInvokedReturnItem() {
        when(itemService.createItem(userId, itemDto)).thenReturn(itemDto);

        assertEquals(itemDto, itemController.itemCreate(userId,itemDto));
        verify(itemService).createItem(userId, itemDto);
    }

    @Test
    public void deleteItemWhenInvokedMethodReturnItem() {
        when(itemService.deleteItem(itemId, userId)).thenReturn(itemDto);

        assertEquals(itemDto, itemController.deleteItem(itemId, userId));
        verify(itemService).deleteItem(itemId, userId);
    }

    @Test
    public void deleteItemWhenItemNotFoundThrowException() {
        when(itemService.deleteItem(itemId, userId)).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> itemController.deleteItem(itemId, userId));
        verify(itemService).deleteItem(itemId, userId);
    }

    @Test
    public void searchWhenInvokedMethodReturnOneItem() {
        List<ItemDto> items = List.of(itemDto);
        RequestItem requestItem = RequestItem.of(userId, 0, 10, "text");

        when(itemService.searchItem(requestItem)).thenReturn(items);

        List<ItemDto> foundItems = itemController.search("text", userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    public void searchWhenItemsNotFoundReturnEmptyList() {
        List<ItemDto> items = List.of();
        RequestItem requestItem = RequestItem.of(userId, 0, 10, "text");

        when(itemService.searchItem(requestItem)).thenReturn(items);

        List<ItemDto> foundItems = itemController.search("text", userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void addCommentWhenMethodInvokedReturnComment() {
        CommentDto commentDto = new CommentDto(1L, userDto, "Name", 1L, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemService.addComment(commentDto, userId, itemId)).thenReturn(commentDto);

        assertEquals(commentDto, itemController.addComment(commentDto, userId, itemId));
    }

    @Test
    public void addCommentWhenUserIsOwnerTheItemThrowException() {
        CommentDto commentDto = new CommentDto(1L, userDto, "Name", 1L, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemService.addComment(commentDto, userId, itemId)).thenThrow(CommentCreateException.class);

        assertThrows(CommentCreateException.class, () -> itemController.addComment(commentDto, userId, itemId));
    }
}
