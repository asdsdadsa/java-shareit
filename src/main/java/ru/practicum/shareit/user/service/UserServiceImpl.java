package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User createUser(User user) {
        log.info("Пользователь создан, " + user);
        return userRepository.createUser(user);
    }

    @Override
    public User patchUser(User user, Integer userId) {
        log.info("Пользователь обновлен, " + user);
        return userRepository.patchUser(user, userId);
    }

    @Override
    public User userById(Integer userId) {
        log.info("Показан пользователь с " + userId + " id.");
        return userRepository.userById(userId);
    }

    @Override
    public List<User> getUsers() {
        log.info("Показаны пользователи " + userRepository.getUsers() + ".");
        return userRepository.getUsers();
    }

    @Override
    public void deleteUser(Integer userId) {
        log.info("Удален пользователь с " + userId + " id.");
        userRepository.deleteUser(userId);
    }
}

