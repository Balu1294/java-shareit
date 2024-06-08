package ru.practicum.shareit.item.model;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @Column(name = "owner_id", nullable = false)
    Integer ownerId;
    ItemRequest request;
}