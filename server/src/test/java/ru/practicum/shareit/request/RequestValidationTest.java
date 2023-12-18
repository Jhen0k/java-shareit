package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RequestValidationTest {
    @InjectMocks
    private RequestValidation requestValidation;

    @Test
    @DisplayName("Валидация ответа")
    void checkValidateResponseTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        assertThrows(ValidationException.class, () -> requestValidation.checkValidateResponse(itemRequestDto));

        itemRequestDto.setDescription("");

        assertThrows(ValidationException.class, () -> requestValidation.checkValidateResponse(itemRequestDto));
    }
}