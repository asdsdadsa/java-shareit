package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {      // Внимание на конструктор при работе с бинами
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email не может быть пустым.");
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))
            throw new ValidationException("Неверный email: " + userDto.getEmail() + ".");
        return toUserDto(userRepository.save(toUser(userDto)));    // именно так return userService.createUser(user);

    }

    @Transactional
    @Override
    public UserDto patchUser(UserDto userDto, Integer userId) {
        User user = userRepository.findById(userId).get();

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return toUserDto(userRepository.save(user));
    }


    @Transactional
    @Override
    public UserDto userById(Integer userId) {
        if (userId == null) {
            throw new NotFoundException("id не может быть null.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User с таким id " + userId + " не найден.");  // !!
        });
        return toUserDto(user);
    }

    @Transactional
    @Override
    public List<UserDto> getUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDtoList.add(toUserDto(user));
        }
        return userDtoList;
    }


    @Transactional
    @Override
    public void deleteUser(Integer userId) {

        userRepository.deleteById(userId);
    }

}


