package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.NotValidException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserClient client;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable("userId") Integer userId) {
        return client.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return client.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto user) {
        return client.add(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user, @Positive @PathVariable("userId") Integer userId) {
        checkValidUserForUpdate(user);
        return client.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable("userId") Integer userId) {
        return client.delete(userId);
    }

    private void checkValidUserForUpdate(UserDto user) {
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !user.getEmail().matches(".+[@].+[\\.].+")) {
                throw new NotValidException(String.format("Email: %s  не прошел валидацию. Email пустой или содержит неверное строение",
                        user.getEmail()));
            }
        }

        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                throw new NotValidException(String.format("Имя пользователя с email: %s пустое", user.getEmail()));
            }
        }
    }
}
