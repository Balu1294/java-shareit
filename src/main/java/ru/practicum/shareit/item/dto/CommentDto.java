package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    Integer id;
    UserDto author;
    String authorName;
    Integer authorId;
    int rating;
    @NotBlank
    String text;
    LocalDateTime created;
    ItemDto item;
    Integer itemId;
}