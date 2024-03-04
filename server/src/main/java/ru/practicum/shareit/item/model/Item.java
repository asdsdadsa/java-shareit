package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor           // ТРЕБОВАЛОСЬ ДЛЯ PATCH
@AllArgsConstructor           // ТРЕБОВАЛОСЬ ДЛЯ PATCH
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column        // nullable = false мб надо
    private Integer id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available") // при Boolean надо is
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")     // manyToOne связи и JoinColumn
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
    // ЕСЛИ ПРОБЛЕМА ЛОГИ ТРЕБУЮТСЯ
}
