package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User createUser(User user);


    User patchUser(User user, Integer userId);


    User userById(Integer userId);


    List<User> getUsers();

    void deleteUser(Integer userId);

}
