package ru.practicum.shareit.item.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer ownerId;
    Integer requestId;
}
