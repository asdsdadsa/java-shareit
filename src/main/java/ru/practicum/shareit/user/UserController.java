package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("User создан, " + userDto);
        return userService.createUser(userDto);                 // return именно так return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {  //  @Valid может ломать осторожно
        log.info("User обновлен, " + userDto);
        return userService.patchUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto userById(@PathVariable Integer userId) {
        log.info("Показан user с " + userId + " id.");
        return userService.userById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Показаны пользователи.");
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Удален user с " + userId + " id.");
        userService.deleteUser(userId);
    }
}
