package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemNotFoundException;
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
    void checkItem() {
        Optional<Item> itemOptional = Optional.empty();

        assertThrows(ItemNotFoundException.class, () -> itemValidation.checkItem(itemOptional));
        itemValidation.checkItem(Optional.of(new Item()));

    }

    @Test
    void checkItemByUser() {
        int userId = 1;
        int itemId = 1;

        doNothing().when(userValidation).checkUser(userId);
        when(itemRepository.existsItemByIdAndOwnerId(userId, itemId)).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> itemValidation.checkItemByUser(userId, itemId));
    }

    @Test
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
