package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {        // Привет, первое ТЗ проекта, а у меня уже проблемы)
    private Integer id;    // Всю логику специально для удобства как в прошлом ТЗ (делал методы по его примеру) поместил в Repository.
    private String name;   // Не понял как без throw new ResponseStatusException(HttpStatus.NOT_FOUND); (нашел в гугле) сделать patchItem() и createItem()
    @Email                 // Так как там выходит ошибка 500 а мне требуется 404
    @NotNull
    private String email;  // примечания в других местах кода это для меня.
}                          // C маппером только в этом ТЗ познакомился и пока делал его идентичным с обычным классом чтобы не путаться.
