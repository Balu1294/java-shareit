package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseDto {

    private Integer id;
    private String name;
    private String description;
    private Integer requestId;
    private Boolean available;
}
