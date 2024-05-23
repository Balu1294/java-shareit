package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;
@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private String request;
}
