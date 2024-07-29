package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class UserDto {
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
