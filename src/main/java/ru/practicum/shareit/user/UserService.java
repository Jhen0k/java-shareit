package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto updateUser(Integer id, UserDto user);

    UserDto findUser(Integer id);

    List<UserDto> getAllUsers();

    void deleteUser(Integer id);
}