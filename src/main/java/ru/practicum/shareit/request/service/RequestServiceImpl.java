package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.NotFoundRequestException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.request.mapper.RequestMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDto addRequest(RequestDto requestDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundUserException(String.format("Пользователя с id: %d не существует", userId)));

        Request request = requestRepository.save(toRequest(requestDto, user));
        return toRequestDto(request);
    }

    @Override
    public RequestDto updateRequest(RequestDto requestDto, Integer userId, Integer requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundUserException(String.format("Пользователя с id: %d не существует", userId)));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new NotFoundRequestException(String.format("Запроса с id: %d не существует", requestId)));

        if (!request.getRequestor().getId().equals(user.getId())) {
            throw new NotFoundUserException("");
        }
        requestDto.setId(requestId);
        Request updatedRequest = requestRepository.save(toRequest(requestDto, user));

        return toRequestDto(updatedRequest);
    }

    @Override
    public List<RequestDto> getRequests() {
        return toRequestDtoList(requestRepository.findAll());
    }

    @Override
    public RequestDto getRequestById(Integer id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundRequestException(String.format("Запроса с id: %d не существует", id)));
        return toRequestDto(request);
    }

    @Override
    public RequestDto deleteRequest(Integer id, Integer userId) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundRequestException(String.format("Запроса с id: %d не существует", id)));
        return toRequestDto(request);
    }
}
