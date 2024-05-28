package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
