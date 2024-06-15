package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .ownerId(item.getOwner().getId() != null ? item.getOwner().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : null)
                .requestId(itemDto.getRequestId() != null ? itemDto.getRequestId() : null)
                .owner(itemDto.getOwnerId() != null ? User.builder().id(itemDto.getOwnerId()).build() : null)
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }
}
