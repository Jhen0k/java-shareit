package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidation;
import ru.practicum.shareit.user.dto.RentUserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingValidation bookingValidation;
    @Mock
    private UserValidation userValidation;
    @Mock
    private BookingMapper bookingMapper;

    private final int itemId = 1;
    private final String start = "2023-12-13T20:12:10";
    private final String end = "2023-12-13T20:12:10";
    private final LocalDateTime startLocalDateTime = LocalDateTime.of(2023, 12, 13, 20, 12, 10);
    private final LocalDateTime endLocalDataTime = LocalDateTime.of(2024, 12, 13, 21, 12, 10);

    @Test
    @DisplayName("Создать бронирование")
    void createBooking() {
        int itemId = 1;
        int userId = 2;
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking = new Booking(4, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.APPROVED);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(1, start, end,
                new RentUserDto(1, "name"), new ItemForBooking(1, "name"), StatusBooking.APPROVED);

        doNothing().when(bookingValidation).checkValidateBooking(bookingRequestDto, userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toEntity(bookingRequestDto, user, item)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDtoFromResponse(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto responseDto = bookingService.createBooking(bookingRequestDto, userId);

        assertEquals(start, responseDto.getStart());
        assertEquals(end, responseDto.getEnd());
    }

    @Test
    @DisplayName("Обновить бронирование")
    void updateStatusBooking() {
        int userId = 2;
        int bookingId = 1;
        boolean isApproved = true;

        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking = new Booking(4, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.REJECTED);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(1, start, end,
                new RentUserDto(1, "name"), new ItemForBooking(1, "name"), StatusBooking.APPROVED);

        doNothing().when(userValidation).checkUser(userId);
        doNothing().when(bookingValidation).checkExistBooking(bookingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDtoFromResponse(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto responseDto = bookingService.updateStatusBooking(userId, bookingId, isApproved);

        assertEquals(bookingResponseDto.getStatus(), responseDto.getStatus());

        assertThrows(ValidationException.class, () -> bookingService.updateStatusBooking(userId, bookingId, null));
        user.setId(1);
        assertThrows(NotFoundException.class, () -> bookingService.updateStatusBooking(userId, bookingId, isApproved));
        user.setId(2);
        booking.setStatusBooking(StatusBooking.APPROVED);
        assertThrows(ValidationException.class, () -> bookingService.updateStatusBooking(userId, bookingId, isApproved));
        bookingService.updateStatusBooking(userId, bookingId, false);
        booking.setStatusBooking(StatusBooking.REJECTED);
        assertThrows(ValidationException.class, () -> bookingService.updateStatusBooking(userId, bookingId, false));


    }

    @Test
    @DisplayName("Поиск бронирования по id")
    void findBooking() {
        int userId = 2;
        int bookingId = 1;
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking = new Booking(4, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.REJECTED);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(1, start, end,
                new RentUserDto(1, "name"), new ItemForBooking(1, "name"), StatusBooking.APPROVED);

        doNothing().when(bookingValidation).checkExistBooking(bookingId);
        doNothing().when(userValidation).checkUser(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDtoFromResponse(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto bookingResponseDto1 = bookingService.findBooking(userId, bookingId);

        assertEquals(bookingResponseDto, bookingResponseDto1);

        assertThrows(NotFoundException.class, () -> bookingService.findBooking(1, bookingId));
    }

    @Test
    @DisplayName("Поиск всех бронирований по пользователю")
    void findBookingsByUser() {
        int userId = 1;
        int bookingId = 2;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = Paginator.getPageable(from, size, "end");
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking1 = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking booking2 = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(booking1, booking2);

        doNothing().when(userValidation).checkUser(userId);
        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> responseDto = bookingService.findBookingsByUser(userId, state, from, size);

        assertEquals(bookings.size(), responseDto.size());
    }

    @Test
    @DisplayName("Поиск бронирования по пользователю и статусу")
    void findBookingsByUser_whenStateCurrent() {
        int userId = 1;
        int bookingId = 2;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 10;
        LocalDateTime start = LocalDateTime.of(2000, 10, 12, 22, 22, 22);
        Pageable pageable = Paginator.getPageable(from, size, "end");
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking1 = new Booking(bookingId, start, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking booking2 = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(booking1, booking2);

        doNothing().when(userValidation).checkUser(userId);
        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> responseDto = bookingService.findBookingsByUser(userId, state, from, size);

        assertEquals(2, responseDto.size());

        state = "PAST";
        booking1.setEnd(LocalDateTime.now().minusHours(4));
        responseDto = bookingService.findBookingsByUser(userId, state, from, size);

        assertEquals(1, responseDto.size());

        state = "FUTURE";
        booking1.setStart(LocalDateTime.now().plusHours(4));
        responseDto = bookingService.findBookingsByUser(userId, state, from, size);

        assertEquals(1, responseDto.size());

        state = "WAITING";
        responseDto = bookingService.findBookingsByUser(userId, state, from, size);

        assertEquals(2, responseDto.size());

        state = "REJECTED";
        booking1.setStatusBooking(StatusBooking.REJECTED);
        responseDto = bookingService.findBookingsByUser(userId, state, from, size);

        assertEquals(1, responseDto.size());

        assertThrows(ValidationException.class, () -> bookingService.findBookingsByUser(userId, "NOT", from, size));
    }

    @Test
    @DisplayName("Поиск всех бронирований по вещам хозяина")
    void findAllBookingsByItemsOwner() {
        int userId = 1;
        int bookingId = 2;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = Paginator.getPageable(from, size, "end");
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking1 = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking booking2 = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(booking1, booking2);

        doNothing().when(userValidation).checkUser(userId);
        when(bookingRepository.findAllBookingsByItemsOwner(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> responseDto = bookingService.findAllBookingsByItemsOwner(userId, state, from, size);

        assertEquals(bookings.size(), responseDto.size());
    }
}
