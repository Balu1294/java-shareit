package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.NotFoundRequestException;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.exception.NotFoundUserException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.HEADER_USER;

@WebMvcTest(controllers = RequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestControllerTest {

    static final String URL = "/requests";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    RequestService requestService;

    int requestId;
    int userId;
    private RequestDto requestDto;

    @BeforeEach
    public void setUp() {
        requestId = 1;
        userId = 1;

        requestDto = RequestDto.builder()
                .id(requestId)
                .authorId(userId)
                .description("Отвертка")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @SneakyThrows
    public void addItemRequestWhenInvokedMethodReturnRequest() {
        when(requestService.addRequest(requestDto, userId)).thenReturn(requestDto);

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) requestId)))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    @SneakyThrows
    public void addItemRequestWhenUserNotFoundReturnStatusIsNotFound() {
        when(requestService.addRequest(requestDto, userId)).thenThrow(NotFoundUserException.class);

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenInvokedMethodReturnOneRequest() {
        List<RequestDto> requests = List.of(requestDto);

        when(requestService.getRequestsForUser(userId)).thenReturn(requests);

        mvc.perform(get(URL)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenInvokedMethodReturnEmptyList() {
        List<RequestDto> requests = List.of();

        when(requestService.getRequestsForUser(userId)).thenReturn(requests);

        mvc.perform(get(URL)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenUserNotFoundReturnStatusIsNotFound() {
        when(requestService.getRequestsForUser(userId)).thenThrow(NotFoundUserException.class);

        mvc.perform(get(URL)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenInvokedMethodReturnOneBooking() {
        List<RequestDto> requests = List.of(requestDto);

        when(requestService.getRequests(any())).thenReturn(requests);

        mvc.perform(get(URL + "/all")
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenInvokedMethodReturnEmptyList() {
        List<RequestDto> requests = List.of();

        when(requestService.getRequests(any())).thenReturn(requests);

        mvc.perform(get(URL + "/all")
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenInvokedMethodReturnRequest() {
        when(requestService.getRequestById(requestId, userId)).thenReturn(requestDto);

        mvc.perform(get(URL + "/{requestId}", requestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) requestId)))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenRequestNotFoundReturnStatusIsNotFound() {
        when(requestService.getRequestById(requestId, userId)).thenThrow(NotFoundItemException.class);

        mvc.perform(get(URL + "/{requestId}", requestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenInvokedMethodReturnRequest() {
        when(requestService.deleteRequest(requestId, userId)).thenReturn(requestDto);

        mvc.perform(delete(URL + "/{requestId}", requestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) requestId)))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenRequestNotFoundReturnStatusIsNotFound() {
        when(requestService.deleteRequest(requestId, userId)).thenThrow(NotFoundRequestException.class);

        mvc.perform(delete(URL + "/{requestId}", requestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }
}
