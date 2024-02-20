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
    @Id                          // Заменил в мапперах на @UtilityClass.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //  @FieldDefaults разобрал.
    @Column
    private Integer id;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @Column
    private LocalDateTime created;

}
