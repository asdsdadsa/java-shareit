package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data                           // Привет. Делал это ТЗ недели 3). Помогли много с мапингом, запросными методами, комментариями и с get методами.
@Builder                        // Но вроде все получилось логично. Остальные // примечания для себя оставил.
@Entity
@NoArgsConstructor           // !!!
@AllArgsConstructor           // !!!
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)    // unique чтобы не duplicate
    private String email;
}
