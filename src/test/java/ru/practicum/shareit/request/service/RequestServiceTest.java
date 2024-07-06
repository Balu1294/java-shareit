package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.NotFoundRequestException;
import ru.practicum.shareit.request.model.GetRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.mapper.RequestMapper.toRequestDto;
import static ru.practicum.shareit.request.mapper.RequestMapper.toRequestDtoList;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceTest {

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    RequestServiceImpl requestService;

    int requestId;
    int authorId;
    int itemId;
    int ownerItemId;
    User user;
    Item item;
    Request request;
    User author;

    @BeforeEach
    public void setUp() {
        requestId = 1;
        authorId = 1;
        itemId = 1;
        ownerItemId = 2;

        user = User.builder()
                .id(ownerItemId)
                .name("Jon Bon")
                .email("mail@mail.com")
                .build();

        author = User.builder()
                .id(authorId)
                .name("Bon Jon")
                .email("google@google.com")
                .build();

        item = Item.builder()
                .id(itemId)
                .owner(user)
                .available(true)
                .name("Отвертка")
                .description("Крутая отвертка")
                .requestId(1)
                .build();

        request = Request.builder()
                .id(requestId)
                .timeOfCreation(LocalDateTime.now())
                .requestor(author)
                .description("отвертка")
                .build();
    }

    @Test
    public void addItemRequestWhenInvokedMethodReturnRequest() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(requestRepository.save(request)).thenReturn(request);

        assertEquals(toRequestDto(request), requestService.addRequest(toRequestDto(request), authorId));
    }

    @Test
    public void addItemRequestWhenUserNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenThrow(NotFoundUserException.class);

        assertThrows(NotFoundUserException.class, () -> requestService.addRequest(toRequestDto(request), authorId));
    }

    @Test
    public void getItemRequestsForUserWhenInvokedMethodReturnOneRequest() {
        List<Request> itemRequests = List.of(request);

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(requestRepository.findAllByRequestorId(authorId)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(toRequestDtoList(itemRequests), requestService.getRequestsForUser(authorId));
    }

    @Test
    public void getItemRequestsForUserWhenUserNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenThrow(NotFoundUserException.class);

        assertThrows(NotFoundUserException.class, () -> requestService.getRequestsForUser(authorId));
    }

    @Test
    public void getItemRequestsForUserWhenRequestsNotFoundReturnEmptyList() {
        List<Request> itemRequests = List.of();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(requestRepository.findAllByRequestorId(authorId)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(0, requestService.getRequestsForUser(authorId).size());
    }

    @Test
    public void getItemRequestByIdWhenInvokedMethodReturnBooking() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(any())).thenReturn(List.of(item));

        assertEquals(toRequestDto(request), requestService.getRequestById(requestId, authorId));
    }

    @Test
    public void getItemRequestByIdWhenRequestNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(requestRepository.findById(requestId)).thenThrow(NotFoundRequestException.class);

        assertThrows(NotFoundRequestException.class, () -> requestService.getRequestById(requestId, authorId));
    }

    @Test
    public void getItemRequestByIdWhenUserNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenThrow(NotFoundUserException.class);

        assertThrows(NotFoundUserException.class, () -> requestService.getRequestById(requestId, authorId));
    }

    @Test
    public void deleteItemRequestWhenInvokedMethodReturnRequest() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertEquals(toRequestDto(request), requestService.deleteRequest(requestId, authorId));
    }

    @Test
    public void deleteItemRequestWhenUserDoesntHaveAccessThrowException() {
        when(userRepository.findById(ownerItemId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(NotFoundUserException.class, () -> requestService.deleteRequest(requestId, ownerItemId));
    }

    @Test
    public void getItemRequestsWhenInvokedMethodReturnOneBooking() {
        List<Request> itemRequests = List.of(request);

        when(userRepository.findById(ownerItemId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdNotLike(ownerItemId, PageRequest.of(0, 10))).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(toRequestDtoList(itemRequests), requestService.getRequests(GetRequest.of(ownerItemId, 0, 10)));
    }

    @Test
    public void getItemRequestsWhenInvokedMethodReturnEmptyList() {
        List<Request> itemRequests = List.of();

        when(userRepository.findById(ownerItemId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdNotLike(ownerItemId, PageRequest.of(0, 10))).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(toRequestDtoList(itemRequests), requestService.getRequests(GetRequest.of(ownerItemId, 0, 10)));
    }
}
