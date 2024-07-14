package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


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

    @PatchMapping("/{id}")
    public UserDto userUpdate(@PathVariable Integer id,
                              @RequestBody UserDto userDto) {
        log.info("Поступил запрос на обновлении данных о пользователе");
        return userService.userUpdate(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        log.info("Поступил запрос на получение пользователя по id.");
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос на получение списка пользователей");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Integer id) {
        log.info("Поступил запрос на удаление пользователя");
        userService.removeUser(id);
    }
}
