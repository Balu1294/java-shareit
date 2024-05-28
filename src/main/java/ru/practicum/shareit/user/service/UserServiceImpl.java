package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.NotFoundUserException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto usercreate(UserDto userDto) {
        return userRepository.usercreate(userDto);
    }

    @Override
    public UserDto userUpdate(Integer id, UserDto userDto) {
        getUserById(id);
        return userRepository.userUpdate(id, userDto);
    }

    @Override
    public UserDto getUserById(Integer id) {
        return userRepository.getUserById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public void removeUser(Integer id) {
        getUserById(id);
        userRepository.removeUser(id);
    }
}
