package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;


    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("User создан, " + userDto);
        return userClient.createUser(userDto);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        log.info("User обновлен, " + userDto);
        return userClient.patchUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {

        log.info("User {} deleted ", userId);
        userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> userById(@PathVariable Integer userId) {
        log.info("Показан user с " + userId + " id.");
        return userClient.userById(userId);
    }


    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Показаны пользователи.");
        return userClient.getUsers();
    }

}
