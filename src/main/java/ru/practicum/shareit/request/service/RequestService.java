package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto, Integer userId);

    RequestDto updateRequest(RequestDto requestDto, Integer userId, Integer requestId);

    List<RequestDto> getRequests();

    RequestDto getRequestById(Integer id);

    RequestDto deleteRequest(Integer id, Integer userId);
}
