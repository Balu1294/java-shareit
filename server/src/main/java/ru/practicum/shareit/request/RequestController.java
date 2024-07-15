package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.GetRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto addRequest(@RequestBody RequestDto request,
                                 @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на создание запроса");
        return requestService.addRequest(request, userId);
    }

    @GetMapping
    public List<RequestDto> getRequestsForUser(@RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на вывод запросов пользователя с id: {}", userId);
        return requestService.getRequestsForUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequestsPageable(@RequestHeader(HEADER_USER) Integer userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на вывод всех запросов");
        return requestService.getRequests(GetRequest.of(userId, from, size));
    }

    @GetMapping(REQUEST_ID_PATH)
    public RequestDto getRequestById(@RequestHeader(HEADER_USER) Integer userId,
                                     @PathVariable(REQUEST_ID) Integer requestId) {
        log.info("Поступил запрос на вывод запроса с id:{}", requestId);
        return requestService.getRequestById(requestId, userId);
    }

    @DeleteMapping(REQUEST_ID_PATH)
    public RequestDto deleteRequest(@PathVariable(REQUEST_ID) Integer requestId,
                                    @RequestHeader(HEADER_USER) Integer userId) {
        log.info("Поступил запрос на удаление запроса");
        return requestService.deleteRequest(requestId, userId);
    }
}
