package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoFull;

import java.util.List;


@Data
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private BookingDtoFull lastBooking;
    private BookingDtoFull nextBooking;
    private List<CommentDto> comments;
}
