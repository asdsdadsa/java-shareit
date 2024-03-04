package ru.practicum.shareit.requset.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    @NotNull
    @NotBlank
    @NotEmpty
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
