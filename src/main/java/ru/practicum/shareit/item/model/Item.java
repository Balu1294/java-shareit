package ru.practicum.shareit.item.model;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    Integer id;
    @Column(name = "name", nullable = false)
    @NotBlank
    String name;
    @Column(name = "description", nullable = false)
    @NotBlank
    String description;
    @Column(name = "available", nullable = false)
    @NotNull
    Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    User owner;
    @Column(name = "request_id", nullable = false)
    Integer requestId;
}