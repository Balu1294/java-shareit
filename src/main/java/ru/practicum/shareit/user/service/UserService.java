package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto usercreate(UserDto userDto);
    UserDto userUpdate(Integer id, UserDto userDto);
    UserDto getUserById(Integer id);
    List<UserDto> getAllUsers();
    void removeUser(Integer id);
}
