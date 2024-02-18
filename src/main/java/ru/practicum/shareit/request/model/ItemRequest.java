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
public class ItemRequest {      // Привет, везде кроме этого класса пояснения для себе оставлял на них можешь не обращать внимание (отстаю потом придется пересматривать для большего понимания).
    @Id                          // Пагинацию вроде бы добавил везде, где говорится в ТЗ. Тесты такого типа первый раз делаю (кроме теории) так что, может быть, будут странности, хотя я перепроверял и не заметил.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
