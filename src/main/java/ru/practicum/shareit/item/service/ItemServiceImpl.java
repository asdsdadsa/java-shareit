package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }


    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {

        if (itemDto.getName() == null || itemDto.getName().isBlank())
            throw new ValidationException("Name не может быть пустым.");
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank())
            throw new ValidationException("Description не может быть пустым.");
        if (itemDto.getAvailable() == null)
            throw new ValidationException("Available не может быть пустым.");

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return toItemDto(item);       // ЕСЛИ ТАК ТО ДАЕТ 500  return toItemDto((itemRepository.save(toItem(itemDto))));
    }

    @Transactional
    @Override
    public ItemDto patchItem(ItemDto itemDto, Integer itemId, Integer userId) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id " + itemId + " не существует"));

        if (!item.getOwner().getId().equals(userId)) {    // 2 !!!
            throw new NotFoundException("Item имеет другого пользователя.");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto itemById(Integer userId, Integer itemId) {

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " не существует"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id " + itemId + " не существует"));
        ItemDto itemDto = toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(toBookingDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(toBookingDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }

        List<Comment> commentList = commentRepository.findAllByItemId(itemId);

        if (!commentList.isEmpty()) {
            itemDto.setComments(toCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> getItems(Integer userId) {

        List<ItemDto> ItemDtoList = new ArrayList<>();

        for (ItemDto itemDto : toItemDtoList(itemRepository.findByOwnerId(userId))) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(toBookingDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(toBookingDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }

            ItemDtoList.add(itemDto);
        }

        for (ItemDto itemDto : ItemDtoList) {

            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(toCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return ItemDtoList;
    }

    @Transactional
    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) return emptyList();
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(Integer itemId, Integer userId, CommentDto commentDto) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id " + itemId + " не существует"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));

        List<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, LocalDateTime.now());

        if (booking.isEmpty()) {
            throw new ValidationException("Невозможно создать комментарий.");
        }

        Comment comment = toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return toCommentDto(comment);
    }
}
