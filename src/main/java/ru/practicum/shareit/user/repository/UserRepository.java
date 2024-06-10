package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {
//    UserDto usercreate(UserDto userDto);
//
//    UserDto userUpdate(Integer id, UserDto userDto);
//
//    UserDto getUserById(Integer id);
//
//    List<UserDto> getAllUsers();
//
//    void removeUser(Integer id);
}
