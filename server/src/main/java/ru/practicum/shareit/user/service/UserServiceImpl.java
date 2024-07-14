package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto usercreate(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user = userRepository.save(user);
        return toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto userUpdate(Integer id, UserDto userDto) {
        User user = UserMapper.toUser(getUserById(id));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        return toUserDto(user);
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователя с id: %d  не существует.", id)));
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ArrayList<>(UserMapper.toUserDtoList(users));
    }

    @Override
    @Transactional
    public UserDto removeUser(Integer id) {
        User user = UserMapper.toUser(getUserById(id));
        userRepository.deleteById(id);
        return toUserDto(user);
    }

}
