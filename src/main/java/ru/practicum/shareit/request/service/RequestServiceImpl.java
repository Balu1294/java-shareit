package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.exception.NotFoundRequestException;
import ru.practicum.shareit.request.model.GetRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.RequestMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDto addRequest(RequestDto requestDto, Integer userId) {
        User user = checkUser(userId);

        requestDto.setCreated(LocalDateTime.now());
        Request request = requestRepository.save(toRequest(requestDto, user));

        return toRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequestsForUser(Integer userId) {
        checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequestorId(userId);
        List<RequestDto> requestDtoList = toRequestDtoList(requests);

        List<Item> items = itemRepository.findAllByRequests(requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList()));

        addResponsesToRequests(requestDtoList, items);

        return requestDtoList.stream()
                .sorted(RequestDto::compareTo)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto getRequestById(Integer id, Integer userId) {
        checkUser(userId);
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundRequestException(String.format("Запроса с id: %d не существует", id)));
        List<Item> items = itemRepository.findAllByRequestId(id);

        RequestDto requestDto = toRequestDto(request);

        List<ResponseDto> response = items.stream()
                .map(item -> new ResponseDto(item.getId(), item.getName(), item.getDescription(),
                        item.getRequestId(), item.getAvailable()))
                .collect(Collectors.toList());
        requestDto.setItems(response);
        return requestDto;
    }

    @Override
    public RequestDto deleteRequest(Integer id, Integer userId) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundRequestException(String.format("Запроса с id: %d не существует", id)));
        User user = checkUser(userId);
        checkAccess(request, user);

        return toRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequests(GetRequest request) {
        User user = checkUser(request.getUserId());
        PageRequest pageable = PageRequest.of(request.getFrom(), request.getSize());

        List<Request> requests = requestRepository.findAllByRequestorIdNotLike(user.getId(), pageable);
        List<RequestDto> requestDtoList = toRequestDtoList(requests);

        List<Item> items = itemRepository.findAllByRequests(requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList()));

        addResponsesToRequests(requestDtoList, items);

        return requestDtoList;
    }

    private User checkUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("Пользователь с id: %d не найден", userId)));
    }

    private void addResponsesToRequests(List<RequestDto> requestDtoList, List<Item> items) {
        for (RequestDto currentRequest : requestDtoList) {
            List<ResponseDto> response = items.stream()
                    .filter(item -> item.getRequestId().equals(currentRequest.getId()))
                    .map(item -> new ResponseDto(item.getId(), item.getName(), item.getDescription(),
                            item.getRequestId(), item.getAvailable()))
                    .collect(Collectors.toList());

            currentRequest.setItems(response);
        }
    }

    private void checkAccess(Request request, User user) {
        if (!request.getRequestor().getId().equals(user.getId())) {
            throw new NotFoundUserException("");
        }
    }
}
