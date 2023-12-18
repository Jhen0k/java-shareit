package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@AllArgsConstructor
public class UserValidation {
    private final UserRepository userRepository;

    public void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Имя не может быть пустым.");
        } else if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Электронная почта указана некорректно");
        }
    }

    @Transactional
    public void checkUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден.");
        }
    }
}
