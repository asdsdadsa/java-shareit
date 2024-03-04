package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private Integer itemId;
    /*    @NotNull
        @FutureOrPresent*/
    private LocalDateTime start;
    /*    @NotNull
        @Future*/
    private LocalDateTime end;
    private Status status;
}
