package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
//    @NotBlank
    String name;
    @Column(unique = true)
//    @NotBlank
    @Email
    String email;
}
