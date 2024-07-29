package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookDto {
    Integer id;
    String name;
    UserDto owner;
    String description;
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
    private Integer requestId;

    public ItemBookDto(Integer id, String name, UserDto user, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.owner = user;
        this.description = description;
        this.available = available;
    }

    public ItemBookDto(Integer id, String name, UserDto user, String description, Boolean available, Integer requestId) {
        this.id = id;
        this.name = name;
        this.owner = user;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
