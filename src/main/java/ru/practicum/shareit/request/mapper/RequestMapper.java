package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(RequestDto requestDto, User user) {
        return new Request(requestDto.getId(),
                requestDto.getDescription(),
                user,
                requestDto.getCreated());
    }

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getTimeOfCreation());
    }

    public static List<RequestDto> toRequestDtoList(List<Request> requests) {
        return requests.stream().map(request -> RequestMapper.toRequestDto(request)).collect(Collectors.toList());
    }
}
