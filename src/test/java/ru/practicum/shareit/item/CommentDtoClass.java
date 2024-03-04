package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoClass {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testItemDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("TextTest")
                .authorName("AuthorNameTest")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("TextTest");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("AuthorNameTest");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();

    }
}
