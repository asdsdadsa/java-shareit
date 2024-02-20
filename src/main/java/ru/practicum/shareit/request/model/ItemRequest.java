package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {      // Подправил. Убрал аннотации логов (во время написания кода логи использую и забываю убрать).
    @Id                         //  Разобрал что такое @UtilityClass, но так как конструктор в мапперах мне вообще не требовался, я так понимаю,
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //  ни в @NoArgsConstructor(access = AccessLevel.PRIVATE) ни в @UtilityClass смысла особого нет. В моем случае.
    @Column                                                       //  @FieldDefaults разобрал.
    private Integer id;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @Column
    private LocalDateTime created;

}
