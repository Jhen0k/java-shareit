package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserValidation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemValidationTest {
    @InjectMocks
    private ItemValidation itemValidation;
    @Mock
    private UserValidation userValidation;
    @Mock
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Получить исключение об отсутствии вещи")
    void checkItem() {
        Optional<Item> itemOptional = Optional.empty();

        assertThrows(NotFoundException.class, () -> itemValidation.checkItem(itemOptional));
        itemValidation.checkItem(Optional.of(new Item()));

    }

    @Test
    @DisplayName("Получить исключение, что не такой вещи у пользователя")
    void checkItemByUser() {
        int userId = 1;
        int itemId = 1;

        doNothing().when(userValidation).checkUser(userId);
        when(itemRepository.existsItemByIdAndOwnerId(userId, itemId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemValidation.checkItemByUser(userId, itemId));
    }

    @Test
    @DisplayName("Получить исключение при валидации полей вещи")
    void checkValidateItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);

        assertThrows(ValidationException.class, () -> itemValidation.checkValidateItem(itemDto));

        itemDto.setName("");

        assertThrows(ValidationException.class, () -> itemValidation.checkValidateItem(itemDto));

        itemDto.setName("name");

        assertThrows(ValidationException.class, () -> itemValidation.checkValidateItem(itemDto));

        itemDto.setDescription("");

        assertThrows(ValidationException.class, () -> itemValidation.checkValidateItem(itemDto));

        itemDto.setDescription("description");

        assertThrows(ValidationException.class, () -> itemValidation.checkValidateItem(itemDto));
    }
}