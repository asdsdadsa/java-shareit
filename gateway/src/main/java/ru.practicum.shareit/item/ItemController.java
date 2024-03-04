package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.createItem(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.patchItem(itemDto, itemId, userId);
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> itemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemClient.itemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return itemClient.getItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text, @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "20") Integer size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Integer itemId, @RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.createComment(itemId, commentDto, userId); // !!!
    }
}