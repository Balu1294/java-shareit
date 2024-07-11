package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.GetRequest;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto, Integer userId);

    List<RequestDto> getRequestsForUser(Integer userId);

    RequestDto getRequestById(Integer id, Integer userId);

    RequestDto deleteRequest(Integer id, Integer userId);

    List<RequestDto> getRequests(GetRequest request);
}
