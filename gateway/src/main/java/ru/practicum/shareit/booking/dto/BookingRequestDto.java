package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class BookingRequestDto {
    @NotNull
    int itemId;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
}
