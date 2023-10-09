package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;

    private UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);                 // return именно так return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {  // !!! @Valid может ломать осторожно
        User user = userService.patchUser(userMapper.toUser(userDto), userId);
        return userMapper.toUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto userById(@PathVariable Integer userId) {
        return userMapper.toUserDto(userService.userById(userId));

    }

    @GetMapping
    public List<UserDto> getUsers() {   // конвертация в дто идет в этом классе поэтому тут и логика хоть она и побольше
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userService.getUsers()) {
            userDtoList.add(userMapper.toUserDto(user));
        }
        return userDtoList;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}
