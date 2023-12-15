package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserValidation;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemValidation {
    private final ItemRepository itemRepository;
    private final UserValidation userValidation;

    public Optional<Item> checkItem(Optional<Item> itemOptional) {
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Вещи с указанным Id не найдено.");
        }
        return itemOptional;
    }

    @Transactional
    public void checkItemByUser(int userId, int itemId) {
        userValidation.checkUser(userId);
        if (!itemRepository.existsItemByIdAndOwnerId(itemId, userId)) {
            throw new NotFoundException("Запрашиваемая вещь отсутствует у данного пользователя.");
        }
    }

    public void checkValidateItem(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Название предмета не может быть пустым");
        } else if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Описание предмета не может быть пустым");
        } else if (item.getAvailable() == null) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Необходимо выбрать статус для предмета");
        }
    }
}
