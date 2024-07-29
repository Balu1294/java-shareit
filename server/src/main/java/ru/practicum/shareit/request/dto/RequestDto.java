package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto implements Comparable<RequestDto> {

    Integer id;
    String description;
    Integer authorId;
    LocalDateTime created;
    List<ResponseDto> items;

    public RequestDto(Integer id, String description, Integer authorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.authorId = authorId;
        this.created = created;
    }

    @Override
    public int compareTo(RequestDto request) {
        return request.getCreated().compareTo(this.created);
    }
}
