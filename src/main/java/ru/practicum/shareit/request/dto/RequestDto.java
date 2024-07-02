package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto implements Comparable<RequestDto> {

    private Integer id;
    @NotBlank
    private String description;
    private Integer authorId;
    private LocalDateTime created;
    private List<ResponseDto> items;

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
