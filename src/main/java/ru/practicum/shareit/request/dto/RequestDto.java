package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class RequestDto {

    private Integer id;
    private String itemName;
    private Integer requestorId;
    private LocalDateTime timeOfCreation;
}
