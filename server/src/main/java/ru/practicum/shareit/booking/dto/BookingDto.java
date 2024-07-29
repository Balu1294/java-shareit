package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingDto {
    Integer id;
    UserDto booker;
    Integer bookerId;
    ItemDto item;
    Integer itemId;
    String itemName;
    LocalDateTime start;
    LocalDateTime end;
    Status status = Status.WAITING;
}
