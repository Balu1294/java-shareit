package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.GetRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.HEADER_USER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto addRequest(@Valid @RequestBody RequestDto request,
                                         @RequestHeader(HEADER_USER) Integer userId) {
        return requestService.addRequest(request, userId);
    }

    @GetMapping
    public List<RequestDto> getRequestsForUser(@RequestHeader(HEADER_USER) Integer userId) {
        return requestService.getRequestsForUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequestsPageable(@RequestHeader(HEADER_USER) Integer userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        return requestService.getRequests(GetRequest.of(userId, from, size));
    }

    @GetMapping("/{request-id}")
    public RequestDto getRequestById(@RequestHeader(HEADER_USER) Integer userId,
                                             @PathVariable("request-id") Integer requestId) {
        return requestService.getRequestById(requestId, userId);
    }

    @DeleteMapping("/{request-id}")
    public RequestDto deleteRequest(@PathVariable("request-id") Integer requestId,
                                            @RequestHeader(HEADER_USER) Integer userId) {
        return requestService.deleteRequest(requestId, userId);
    }
}
