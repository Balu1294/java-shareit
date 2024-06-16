package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDto {

    private Integer id;
    private UserDto author;
    private int rating;
    @NotBlank
    private String text;
    private LocalDateTime created;
    private ItemDto item;
}