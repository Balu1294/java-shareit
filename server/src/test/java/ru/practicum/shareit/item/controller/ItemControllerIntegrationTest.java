package ru.practicum.shareit.item.controller;

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
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.HEADER_USER;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ItemControllerIntegrationTest {

    private static final String URL = "/items";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private int userId;
    private int itemId;
    private int newUserId;
    private int unknownUserId;
    private int unknownItemId;
    private int commentId;
    private ItemBookDto itemBookDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDtoNew;
    private CommentDto commentDto;

    @BeforeEach
    public void setUp() {
        unknownUserId = 100;
        unknownItemId = 100;
        userId = 1;
        newUserId = 2;
        itemId = 1;
        commentId = 1;

        userDto = UserDto.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Jon Bon")
                .build();

        userDtoNew = UserDto.builder()
                .id(newUserId)
                .name("Bon Jon")
                .email("google@mail.com")
                .build();

        itemBookDto = ItemBookDto.builder()
                .id(itemId)
                .available(true)
                .name("Отвертка")
                .owner(userDto)
                .description("Простая отвертка")
                .requestId(1)
                .build();

        itemDto = ItemDto.builder()
                .id(itemId)
                .ownerId(userDto.getId())
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true)
                .requestId(1)
                .build();

        commentDto = CommentDto.builder()
                .item(itemDto)
                .author(userDtoNew)
                .created(LocalDateTime.now())
                .rating(1)
                .text("text")
                .authorName("Bon Jon")
                .build();
    }

    @Test
    @SneakyThrows
    public void getItemByIdWhenMethodInvokedReturnItem() {
        mvc.perform(get(URL + "/{itemId}", itemId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is(itemBookDto.getName())))
                .andExpect(jsonPath("$.owner.id", is(userId)))
                .andExpect(jsonPath("$.description", is(itemBookDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemBookDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void getItemByIdWhenItemNotFoundReturnStatusNotFound() {
        mvc.perform(get(URL + "/{itemId}", unknownItemId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenFoundThreeReturnListWithThreeItems() {
        mvc.perform(get(URL)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenFoundThreeItemsAndSizeIsOneReturnListWithOneItem() {
        mvc.perform(get(URL)
                        .header(HEADER_USER, userId)
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenFoundThreeItemsAndFromIsOneAndSizeIs2ReturnListWithTwoItems() {
        mvc.perform(get(URL)
                        .header(HEADER_USER, userId)
                        .queryParam("size", "2")
                        .queryParam("from", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenItemsNotFoundReturnEmptyList() {
        mvc.perform(get(URL)
                        .header(HEADER_USER, newUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenNameChangedReturnUpdatedItem() {
        ItemDto updateItem = ItemDto.builder()
                .name("Топор")
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is(updateItem.getName())))
                .andExpect(jsonPath("$.ownerId", is(userId)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenNameChangedAndNameIsBlankReturnStatusBadRequest() {
        ItemDto updateItem = ItemDto.builder()
                .name("")
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void updateItemWhenDescriptionChangedReturnUpdatedItem() {
        ItemDto updateItem = ItemDto.builder()
                .description("Простая отвертка")
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.ownerId", is(userId)))
                .andExpect(jsonPath("$.description", is(updateItem.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenAvailableChangedReturnUpdatedItem() {
        ItemDto updateItem = ItemDto.builder()
                .available(false)
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.ownerId", is(userId)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItem.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenItemNotFoundReturnStatusIsNotFound() {
        ItemDto updateItem = ItemDto.builder()
                .available(false)
                .build();

        mvc.perform(patch(URL + "/{itemId}", unknownItemId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void addItemWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(post(URL)
                        .header(HEADER_USER, unknownUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void addItemWhenNameIsBlankReturnStatusIsBadRequest() {
        itemDto.setName("");

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addItemWhenDescriptionIsBlankReturnStatusIsBadRequest() {
        itemDto.setDescription("");

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addItemWhenRequestIdIsNegativeReturnStatusIsBadRequest() {
        itemDto.setRequestId(-1);

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void deleteItemWhenInvokedMethodReturnItem() {
        mvc.perform(delete(URL + "/{itemId}", itemId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.ownerId", is(userId)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void deleteItemWhenItemNotFoundReturnStatusIsNotFound() {
        mvc.perform(delete(URL + "/{itemId}", unknownItemId)
                        .header(HEADER_USER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void searchWhenFoundOneItemReturnListWithOneItem() {
        mvc.perform(get(URL + "/search")
                        .header(HEADER_USER, userId)
                        .queryParam("text", "Отвертка"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemId)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].ownerId", is(userId)))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void searchWhenNotFoundItemsReturnEmptyList() {
        mvc.perform(get(URL + "/search")
                        .header(HEADER_USER, userId)
                        .queryParam("text", "Стремянка"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void searchWhenFoundTwoItemsAndSizeIsOneReturnListWithOneItem() {
        mvc.perform(get(URL + "/search")
                        .header(HEADER_USER, userId)
                        .queryParam("text", "Отвертка")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemId)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].ownerId", is(userId)))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void addCommentWhenInvokedMethodReturnComment() {
        mvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header(HEADER_USER, newUserId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentId)))
                .andExpect(jsonPath("$.authorId", is(newUserId)))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    @SneakyThrows
    public void addCommentWhenUserIsOwnerTheItemReturnStatusIs() {
        mvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addCommentWhenUserDoesntHaveBookingTheItemReturnStatusIs() {
        int userIdWithoutBooking = 3;

        mvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header(HEADER_USER, userIdWithoutBooking)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
