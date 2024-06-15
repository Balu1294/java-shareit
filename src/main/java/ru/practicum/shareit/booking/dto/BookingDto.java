package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class BookingDto {
    private Integer id;
    private UserDto booker;
    private ItemDto item;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Status status = Status.WAITING;
}
