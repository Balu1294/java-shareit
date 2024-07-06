package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDtoList;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserServiceImpl service;

    @Captor

    ArgumentCaptor<User> argumentCaptor;

    User exceptedUser;
    int userId;

    @BeforeEach
    public void fillUsers() {
        userId = 1;
        exceptedUser = new User(userId, "Jon Bon", "mail@mail.ru");
    }

    @Test
    public void getUserByIdWhenUserFoundReturnUser() {
        when(repository.findById(userId)).thenReturn(Optional.of(exceptedUser));

        UserDto foundUser = service.getUserById(userId);

        assertEquals(toUserDto(exceptedUser), foundUser);
    }

    @Test
    public void getUserByIdWhenUserNotFoundThrowException() {
        int userId = 0;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> service.getUserById(userId));
    }

    @Test
    public void getUsersWhenOneUserFoundReturnListWithOneUser() {
        List<User> users = List.of(exceptedUser);
        when(repository.findAll()).thenReturn(users);

        List<UserDto> foundUsers = service.getAllUsers();

        assertEquals(toUserDtoList(users), foundUsers);
        assertEquals(1, foundUsers.size());
    }

    @Test
    public void getUsersWhenUsersNotFoundReturnEmptyList() {
        List<User> users = List.of();
        when(repository.findAll()).thenReturn(users);

        List<UserDto> foundUsers = service.getAllUsers();

        assertEquals(toUserDtoList(users), service.getAllUsers());
        assertEquals(0, foundUsers.size());
    }

    @Test
    public void addUserWhenUserAddReturnUser() {
        when(repository.save(exceptedUser)).thenReturn(exceptedUser);

        assertEquals(toUserDto(exceptedUser), service.usercreate(toUserDto(exceptedUser)));
        verify(repository).save(exceptedUser);
    }

    @Test
    public void updateUserWhenUserUpdateReturnUpdatedUser() {
        User newUser = new User();
        newUser.setEmail("newMail@mail.ru");
        newUser.setName("newName Jon");

        when(repository.findById(userId)).thenReturn(Optional.of(exceptedUser));

        service.userUpdate(userId, toUserDto(newUser));

        verify(repository).save(argumentCaptor.capture());
        User updatedUser = argumentCaptor.getValue();

        assertEquals("newMail@mail.ru", updatedUser.getEmail());
        assertEquals("newName Jon", updatedUser.getName());
    }

    @Test
    public void updateUserWhenUserNotFoundThrowException() {
        int userId = 0;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> service.userUpdate(userId, toUserDto(exceptedUser)));
    }

    @Test
    public void deleteUserWhenUserDeletedDontThrowException() {
        when(repository.findById(userId)).thenReturn(Optional.of(exceptedUser));

        assertEquals(toUserDto(exceptedUser), service.removeUser(userId));
        verify(repository).deleteById(userId);
    }

    @Test
    public void deleteUserWhenUserNotFoundThrowException() {
        int userId = 0;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> service.removeUser(userId));
        verify(repository, never()).deleteById(userId);
    }
}
