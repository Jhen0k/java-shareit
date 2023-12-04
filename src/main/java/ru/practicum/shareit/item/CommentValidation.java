package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CommentValidation {
    public void checkValidateComment(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            log.error("Ошибка добавления комментария");
            throw new ValidationException("Комментарий не может быть пустым.");
        }
        commentDto.setCreated(LocalDateTime.now());
    }
}
