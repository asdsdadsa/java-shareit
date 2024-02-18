/*package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {

    private Map<Integer, User> users = new HashMap<>();

    private Integer id = 1;

    @Override
    public User createUser(User user) {
        for (User userCheck : users.values()) {
            if (userCheck.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Этот email уже используется.");
            }
        }

        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }


    @Override
    public User patchUser(User user, Integer userId) {
        User userPatch = users.get(userId);

        for (User userCheck : users.values()) {
            if (userCheck.getEmail().equals(user.getEmail()) && !userCheck.getId().equals(userId)) { // !! ДОП УСЛОВИЕ !!
                throw new ValidationException("Этот email уже используется.");
            }
        }

        if (user.getName() != null) {
            userPatch.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userPatch.setEmail(user.getEmail());
        }

        users.put(userId, userPatch);
        return userPatch;
    }

    @Override
    public User userById(Integer userId) {        // !! Если используется в другом методе !! внимание
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("Пользователь с  id " + userId + " не найден.");
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer userId) {
        users.remove(userId);
    }
}*/
