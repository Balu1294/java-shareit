package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.HEADER_USER;

@SpringBootTest(properties = "db.name = test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RequestControllerIntegrationTest {

    private static final String URL = "/requests";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private RequestDto itemRequestDto;
    private int userId;
    private int unknownUserId;
    private int unknownRequestId;
    private int requestId;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        now = LocalDateTime.now();
        userId = 2;
        requestId = 1;
        unknownUserId = 100;
        unknownRequestId = 100;

        itemRequestDto = RequestDto.builder()
                .id(requestId)
                .authorId(userId)
                .description("Бензопила")
                .created(now)
                .build();
    }

    @Test
    @SneakyThrows
    public void addItemRequestWhenInvokedMethodReturnRequest() {
        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId)))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    @SneakyThrows
    public void addItemRequestWhenUserNotFoundReturnRequest() {
        mvc.perform(post(URL)
                        .header(HEADER_USER, unknownUserId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenInvokedMethodReturnThreeRequestWithResponses() {
        mvc.perform(get(URL)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].items.length()", is(0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].items.length()", is(1)))
                .andExpect(jsonPath("$[2].id", is(1)))
                .andExpect(jsonPath("$[2].items.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(get(URL)
                        .header(HEADER_USER, unknownUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenUserDoesntHaveRequestsReturnEmptyList() {
        int userIdWithoutRequests = 3;

        mvc.perform(get(URL)
                        .header(HEADER_USER, userIdWithoutRequests))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenInvokedMethodReturnRequest() {
        mvc.perform(delete(URL + "/{requestId}", requestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId)));
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenRequestNotFoundReturnStatusNotFound() {
        mvc.perform(delete(URL + "/{requestId}", unknownRequestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenUserIsNotOwnerOfRequestReturnStatusNotFound() {
        int notOwnerId = 3;

        mvc.perform(delete(URL + "/{requestId}", unknownRequestId)
                        .header(HEADER_USER, notOwnerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenInvokedMethodReturnThreeRequests() {
        int userWithoutRequests = 1;

        mvc.perform(get(URL + "/all")
                        .header(HEADER_USER, userWithoutRequests)
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenSizeIsTwoReturnTwoRequests() {
        int userWithoutRequests = 1;

        mvc.perform(get(URL + "/all")
                        .header(HEADER_USER, userWithoutRequests)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenUserNotFoundReturnStatusNotFound() {
        mvc.perform(get(URL + "/all")
                        .header(HEADER_USER, unknownUserId)
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenInvokedMethodReturnRequest() {
        mvc.perform(get(URL + "/{requestId}", requestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) requestId)))
                .andExpect(jsonPath("$.items.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenRequestNotFoundReturnStatusIsNotFound() {
        mvc.perform(get(URL + "/{requestId}", unknownRequestId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenRequestDoesntHaveResponseReturnRequest() {
        int requestIdWithoutResponse = 3;

        mvc.perform(get(URL + "/{requestId}", requestIdWithoutResponse)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) requestIdWithoutResponse)))
                .andExpect(jsonPath("$.items.length()", is(0)));
    }
}
