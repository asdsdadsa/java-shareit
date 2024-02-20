package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository, UserService userService, ItemRequestRepository itemRequestRepository) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.itemRequestRepository = itemRequestRepository;
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


        if (itemDto.getRequestId() != null) {                        // ТЗ 15
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get()); // ТЗ 15
        } // ТЗ 15

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
    public List<ItemDto> getItems(Integer userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        List<ItemDto> itemDtoList = new ArrayList<>();

        for (ItemDto itemDto : toItemDtoList(itemRepository.findByOwnerId(userId, pageable))) {

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

            itemDtoList.add(itemDto);
        }

        for (ItemDto itemDto : itemDtoList) {

            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(toCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return itemDtoList;
    }

    @Transactional
    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        if (text.isBlank()) return emptyList();
        return itemRepository.search(text, pageable)
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
