package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentValidationTest {
    @InjectMocks
    private CommentValidation commentValidation;

    @Test
    void checkValidateComment() {
        CommentDto commentDto = new CommentDto(1, null, "name", null);

        assertThrows(ValidationException.class, () -> commentValidation.checkValidateComment(commentDto));

        commentDto.setText("");

        assertThrows(ValidationException.class, () -> commentValidation.checkValidateComment(commentDto));

        commentDto.setText("text");

        commentValidation.checkValidateComment(commentDto);

        assertNotNull(commentDto);
    }
}
