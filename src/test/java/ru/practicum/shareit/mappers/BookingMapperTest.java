package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.RentUserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {
    @InjectMocks
    private BookingMapperImpl bookingMapper;
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private ItemMapperImpl itemMapper;

    @Test
    void toEntity() {
        int itemId = 1;
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        BookingRequestDto bookingRequestDto = new BookingRequestDto("2023-12-13T16:18:00", "2023-12-13T16:18:00", itemId);

        Booking booking = bookingMapper.toEntity(bookingRequestDto, user, item);

        assertEquals(booking.getId(), 0);
        assertEquals(booking.getBooker(), user);
        assertEquals(booking.getItem(), item);
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
    }

    @Test
    void toDtoFromResponse() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        RentUserDto rentUserDto = new RentUserDto(1, "name");
        Item item = new Item(1, user, "name", "description", true);
        ItemForBooking itemForBooking = new ItemForBooking(1, "name");
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(), user, item, StatusBooking.APPROVED);

        when(userMapper.toRentUserDto(user)).thenReturn(rentUserDto);
        when(itemMapper.toItemForBooking(item)).thenReturn(itemForBooking);

        BookingResponseDto bookingResponseDto = bookingMapper.toDtoFromResponse(booking);

        assertEquals(bookingResponseDto.getId(), booking.getId());
        assertEquals(bookingResponseDto.getBooker(), rentUserDto);
        assertEquals(bookingResponseDto.getItem(), itemForBooking);
        assertNotNull(bookingResponseDto.getStart());
        assertNotNull(bookingResponseDto.getEnd());
    }

    @Test
    void toBookingOwnerByItem() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now(), user, item, StatusBooking.APPROVED);

        BookingOwnerByItem bookingOwnerByItem = bookingMapper.toBookingOwnerByItem(booking);

        assertEquals(bookingOwnerByItem.getId(), booking.getId());
        assertEquals(bookingOwnerByItem.getBookerId(), booking.getBooker().getId());
        assertNotNull(bookingOwnerByItem.getStart());
        assertNotNull(bookingOwnerByItem.getEnd());
    }
}
