package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    public Integer id;
    /*    @NotNull
        @NotBlank*/
    private String text;
    private LocalDateTime created;
    private String authorName;
}
