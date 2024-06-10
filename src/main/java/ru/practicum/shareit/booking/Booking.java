package ru.practicum.shareit.booking;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    private int id;
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @NotNull
    @JoinColumn(name = "item_id")
    private Item item;
    @NotNull
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @NotBlank
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}
