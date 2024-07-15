package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.USER_ID;
import static ru.practicum.shareit.constant.Constant.USER_ID_PATH;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto userCreate(@RequestBody UserDto userDto) {
        log.info("Поступил запрос на создание пользователя");
        return userService.usercreate(userDto);
    }

    @PatchMapping(USER_ID_PATH)
    public UserDto userUpdate(@PathVariable(USER_ID) Integer id,
                              @RequestBody UserDto userDto) {
        log.info("Поступил запрос на обновлении данных о пользователе");
        return userService.userUpdate(id, userDto);
    }

    @GetMapping(USER_ID_PATH)
    public UserDto getUserById(@PathVariable(USER_ID) Integer id) {
        log.info("Поступил запрос на получение пользователя по id.");
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос на получение списка пользователей");
        return userService.getAllUsers();
    }

    @DeleteMapping(USER_ID_PATH)
    public void removeUser(@PathVariable(USER_ID) Integer id) {
        log.info("Поступил запрос на удаление пользователя");
        userService.removeUser(id);
    }
}
