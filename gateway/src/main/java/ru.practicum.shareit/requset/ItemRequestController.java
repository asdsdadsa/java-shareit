package ru.practicum.shareit.requset;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requset.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.USER_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody @Valid ItemRequestDto itemRequestDto, @RequestHeader(USER_HEADER) Integer userId) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping({"/{requestId}"})
    public ResponseEntity<Object> requestById(@PathVariable Integer requestId, @RequestHeader(USER_HEADER) Integer userId) {
        return itemRequestClient.requestById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_HEADER) Integer userId) {
        return itemRequestClient.getRequests(userId);
    }


    @GetMapping({"/all"})     // ВНИМАТЕЛЬНО ЧТОБЫ НЕ ПОСТАВИТЬ {} КОГДА НЕ НАДО
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_HEADER) Integer userId, @PositiveOrZero @RequestParam(defaultValue = "0")
    Integer from, @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }


}
