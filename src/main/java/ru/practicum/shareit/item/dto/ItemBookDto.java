package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookDto {
    Integer id;
    @NotBlank
    String name;
    UserDto owner;
    @NotBlank
    String description;
    @NotNull
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
    public ItemBookDto(Integer id, String name, UserDto user, String description, Boolean available,
                       BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.owner = user;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
