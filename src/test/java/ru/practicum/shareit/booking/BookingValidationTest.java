package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemValidation;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserValidation;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingValidationTest {
    @InjectMocks
    private BookingValidation bookingValidation;
    @Mock
    private UserValidation userValidation;
    @Mock
    private ItemValidation itemValidation;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemService itemService;

    private final String start = "2023-12-13T20:12:10";
    private final String end = "2023-12-13T20:12:10";

    @Test
    void checkValidateBooking() {
        int itemId = 1;
        int userId = 1;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        User user = User.builder().id(null).name("name").email("mail@mail.ru").build();
        UserDto userDto = UserDto.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        BookingOwnerByItem bookingOwnerByItem = new BookingOwnerByItem(1, "2023-12-13T18:00:00", "2023-12-13T18:00:00", 1);
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(1, userDto, "name",
                "description", true, new ItemRequest(), new ArrayList<>(), bookingOwnerByItem, bookingOwnerByItem);

        doNothing().when(userValidation).checkUser(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemValidation.checkItem(Optional.of(item))).thenReturn(Optional.of(item));
        when(itemService.findItem(userId)).thenReturn(itemWithBookingsDto);

        assertThrows(NotFoundException.class, () -> bookingValidation.checkValidateBooking(bookingRequestDto, userId));

        userDto.setId(2);
        when(itemRepository.existsItemByIdAndAvailableIsTrue(itemId)).thenReturn(false);

        assertThrows(ValidationException.class, () -> bookingValidation.checkValidateBooking(bookingRequestDto, userId));

        when(itemRepository.existsItemByIdAndAvailableIsTrue(itemId)).thenReturn(true);
        bookingRequestDto.setStart(null);
        bookingRequestDto.setStart(null);

        assertThrows(ValidationException.class, () -> bookingValidation.checkValidateBooking(bookingRequestDto, userId));

        bookingRequestDto.setStart(end);
        bookingRequestDto.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingValidation.checkValidateBooking(bookingRequestDto, userId));

        bookingRequestDto.setStart("2022-12-13T20:12:10");
        bookingRequestDto.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingValidation.checkValidateBooking(bookingRequestDto, userId));
    }

    @Test
    void checkExistBooking() {
        int bookingId = 1;

        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingValidation.checkExistBooking(bookingId));
    }
}
