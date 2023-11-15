package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto patchUser(UserDto userDto, Integer userId);

    UserDto userById(Integer userId);

    List<UserDto> getUsers();

    void deleteUser(Integer userId);
}
