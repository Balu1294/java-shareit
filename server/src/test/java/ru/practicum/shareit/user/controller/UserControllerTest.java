package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mvc;

    UserDto exceptedUser;
    int userId;

    @BeforeEach
    public void fillUsers() {
        userId = 1;
        exceptedUser = new UserDto(userId, "Jon Bon", "mail@mail.ru");
    }

    @Test
    @SneakyThrows
    public void getUserByIdWhenUserNotFoundThrowException() {
        when(userService.getUserById(userId)).thenThrow(NotFoundUserException.class);

        mvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @Test
    @SneakyThrows
    public void getUserByIdWhenUserFoundReturnDefaultUser() {
        mvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @Test
    @SneakyThrows
    public void getUsersWhenOneUserFoundReturnListWithOneUser() {
        List<UserDto> users = List.of(exceptedUser);
        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(userService).getAllUsers();
    }

    @Test
    @SneakyThrows
    public void getUsersWhenUsersNotFoundReturnEmptyList() {
        List<UserDto> users = List.of();
        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));

        verify(userService).getAllUsers();
    }

    @Test
    @SneakyThrows
    public void addUserWhenUserIsValidReturnUser() {
        when(userService.usercreate(exceptedUser)).thenReturn(exceptedUser);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(exceptedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Jon Bon")))
                .andExpect(jsonPath("$.email", is("mail@mail.ru")));

        verify(userService).usercreate(exceptedUser);
    }

    @Test
    @SneakyThrows
    public void addUserWhenNameIsNotValidThrowException() {
        exceptedUser.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(exceptedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).usercreate(exceptedUser);
    }

    @Test
    @SneakyThrows
    public void addUserWhenEmailIsNotValidThrowException() {
        exceptedUser.setEmail("not valid mail");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(exceptedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).usercreate(exceptedUser);
    }

    @Test
    @SneakyThrows
    public void addUserWhenEmailIsBlankThrowException() {
        exceptedUser.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(exceptedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).usercreate(exceptedUser);
    }

    @Test
    @SneakyThrows
    public void updateUserWhenUserIsValidReturnUpdatedUser() {
        UserDto updateUser = new UserDto(userId, "Update jon", "update@mail.com");
        when(userService.userUpdate(userId, updateUser)).thenReturn(updateUser);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Update jon")))
                .andExpect(jsonPath("$.email", is("update@mail.com")));

    }

    @Test
    @SneakyThrows
    public void deleteUserWhenUserNotFoundThrowException() {
        when(userService.removeUser(userId)).thenThrow(NotFoundUserException.class);

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}
