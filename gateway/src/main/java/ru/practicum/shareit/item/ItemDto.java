package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemDto {
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    @Positive
    Integer requestId;
}
