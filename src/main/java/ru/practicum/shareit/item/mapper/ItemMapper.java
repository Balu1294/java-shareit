package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

public class ItemMapper {
    public static ItemBookDto toItemBookDto(Item item) {
        return ItemBookDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(toUserDto(item.getOwner()))
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequestId())
                .build();
    }

    public static Item toItem(ItemBookDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : null)
                .owner(user)
                .requestId(itemDto.getRequestId())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : null)
                .owner(user)
                .requestId(itemDto.getRequestId())
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }
}
