package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemBookDto toItemBookDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        return new ItemBookDto(item.getId(),
                item.getName(),
                UserMapper.toUserDto(item.getOwner()),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments);
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
//                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .ownerId(item.getOwner().getId() != null ? item.getOwner().getId() : null)
                .build();
    }

//    public static ItemBookDto toItemBookDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
//        Integer itemId = item.getId();
//        String name = item.getName();
//        User owner = item.getOwner();
//        String description = item.getDescription();
//        Boolean available = item.getAvailable();
//
//        return new ItemBookDto(itemId, name, UserMapper.toUserDto(owner), description, available, lastBooking, nextBooking, comments);
//    }

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : null)
//                .requestId(itemDto.getRequestId() != null ? itemDto.getRequestId() : null)
                .owner(user)
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }
}
