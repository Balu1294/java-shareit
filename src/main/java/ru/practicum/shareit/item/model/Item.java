package ru.practicum.shareit.item.model;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@RequiredArgsConstructor
public class Item {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
    @NotNull
    private User owner;
    private ItemRequest request;
}
