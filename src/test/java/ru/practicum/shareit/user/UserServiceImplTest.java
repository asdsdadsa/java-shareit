package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    private User userFirst;
    private User userSecond;
    private UserDto userDtoFirst;
    private UserDto userDtoSecond;

    @BeforeEach
    void setUp() {
        userFirst = User.builder()
                .id(1)
                .name("John")
                .email("john.doe@mail.com")
                .build();

        userDtoFirst = toUserDto(userFirst);

        userSecond = User.builder()
                .id(2)
                .name("Ivan")
                .email("ivan@mail.com")
                .build();

        userDtoSecond = toUserDto(userSecond);
    }


    @Test
    void createUserTest() {

        when(userRepository.save(any(User.class))).thenReturn(userFirst);
        UserDto createdUserDto = userService.createUser(userDtoFirst);

        assertEquals(userFirst.getId(), createdUserDto.getId());
        assertEquals(userFirst.getName(), createdUserDto.getName());
        assertEquals(userFirst.getEmail(), createdUserDto.getEmail());

        verify(userRepository, times(1)).save(userFirst);
    }

    @Test
    void patchUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userFirst));      // если в методе сервиса вызывается метод репозитория его надо вызвать и в тесте
        when(userRepository.save(any(User.class))).thenReturn(userFirst);

        userDtoFirst.setName("JohnUpdate");
        userDtoFirst.setEmail("johnUpdate.doe@mail.com");

        UserDto userDtoUpdated = userService.patchUser(userDtoFirst, 1);

        assertEquals(userFirst.getName(), userDtoUpdated.getName());
        assertEquals(userFirst.getEmail(), userDtoUpdated.getEmail());

        verify(userRepository, times(1)).save(userFirst);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void userByIdTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userFirst));
        UserDto createdUserDto = userService.userById(1);

        assertEquals(userFirst.getId(), createdUserDto.getId());
        assertEquals(userFirst.getName(), createdUserDto.getName());
        assertEquals(userFirst.getEmail(), createdUserDto.getEmail());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(userFirst, userSecond));

        List<UserDto> userDtoList = userService.getUsers();

        assertEquals(List.of(userDtoFirst, userDtoSecond), userDtoList);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(1);
    }
}