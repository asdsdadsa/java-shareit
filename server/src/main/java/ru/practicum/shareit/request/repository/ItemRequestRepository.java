package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Integer userId);

    List<ItemRequest> findAllByRequesterIdNot(Integer userId, Pageable pageable); // Not отрицание. Все кроме этого значения

}
