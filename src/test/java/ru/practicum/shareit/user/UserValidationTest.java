package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    @InjectMocks
    private UserValidation userValidation;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Валидация пользователя")
    public void validateUserTest() {
        int userId = 1;
        User userNameIsNull = new User(userId, null, "Jon");
        User userNameIsBlank = new User(userId, "join@mail.ru", "");
        User userEmailIsNull = new User(userId, null, "Jon");
        User userEmailIsBlank = new User(userId, "joinMail.ru", "Jon");


        assertThrows(ValidationException.class, () -> userValidation.validateUser(userNameIsNull));
        assertThrows(ValidationException.class, () -> userValidation.validateUser(userNameIsBlank));
        assertThrows(ValidationException.class, () -> userValidation.validateUser(userEmailIsNull));
        assertThrows(ValidationException.class, () -> userValidation.validateUser(userEmailIsBlank));
    }

    @Test
    @DisplayName("Существует ли пользователь.")
    public void checkUser() {
        int userId = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userValidation.checkUser(userId));
    }
}
