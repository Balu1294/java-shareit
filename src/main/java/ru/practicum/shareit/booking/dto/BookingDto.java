package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class BookingDto {
    private Integer id;
    private UserDto booker;
    private Integer bookerId;
    private ItemDto item;
    @NotNull
    private Integer itemId;
    private  String itemName;
    @NotNull
    @Future
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    private Status status = Status.WAITING;
}
