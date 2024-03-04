package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    // Этот класс тестов с помощью аннотации @DataJpaTest нужен если в классе есть @Query метод
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private Item itemFirst;
    private Item itemSecond;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .name("John")
                .email("john.doe@mail.com")
                .build();

        userRepository.save(user);

        itemFirst = Item.builder()
                .id(1)
                .name("ItemTest")
                .description("DescriptionTest")
                .available(true)
                .owner(user)
                .build();

        itemRepository.save(itemFirst);

        itemSecond = Item.builder()
                .id(2)
                .name("searchTest")
                .description("DescriptionTest search")
                .available(true)
                .owner(user)
                .build();

        itemRepository.save(itemSecond);
    }

    @Test
    void search() {
        Pageable pageable = PageRequest.of(0, 20);

        List<Item> items = itemRepository.search("search", pageable);

        assertEquals(1, items.size());
        assertEquals("searchTest", items.get(0).getName());
        assertEquals("DescriptionTest search", items.get(0).getDescription());
    }

}
