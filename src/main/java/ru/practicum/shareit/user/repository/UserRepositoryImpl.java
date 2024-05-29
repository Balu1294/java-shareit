package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailDuplicateException;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users;
    private final Set<String> emails = new HashSet<>();
    private int idGenerator = 1;

    @Override
    public UserDto usercreate(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        checkEmail(user);
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        log.info("Пользователь создан");
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto userUpdate(Integer id, UserDto userDto) {
        User user = UserMapper.toUser(getUserById(id));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!user.getEmail().equals(userDto.getEmail())) {
                checkEmail(UserMapper.toUser(userDto));
                emails.remove(user.getEmail());
                user.setEmail(userDto.getEmail());
                emails.add(user.getEmail());
            }
        }
        users.put(user.getId(), user);
        log.info("Данные о пользователе обновлены");
        return getUserById(id);
    }

    @Override
    public UserDto getUserById(Integer id) {
        List<User> userList = new ArrayList<>(users.values());
        User user = userList.stream()
                .filter(us -> us.getId().equals(id))
                .findFirst().orElseThrow(() ->
                        new NotFoundUserException("Пользователя с id= " + id + " не существует"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUser(Integer id) {
        emails.remove(getUserById(id).getEmail());
        users.remove(id);
        log.info("Пользователь удален");
    }

    public void checkEmail(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailDuplicateException(String.format("Пользователь с email  %s уже существует", user.getEmail()));
        }
    }
}
