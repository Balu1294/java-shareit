package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserDto usercreate(UserDto userDto);

    UserDto userUpdate(Integer id, UserDto userDto);

    UserDto getUserById(Integer id);

    List<UserDto> getAllUsers();

    void removeUser(Integer id);
}
