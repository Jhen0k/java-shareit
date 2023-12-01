package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;


    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, int userId) {
        checkValidateBooking(bookingRequestDto, userId);
        User user = userRepository.findById(userId).get();
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow();
        Booking booking = bookingMapper.toEntity(bookingRequestDto, user, item);
        return bookingMapper.toDtoFromResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto updateStatusBooking(int userId, int bookingId, Boolean approved) {
        checkUser(userId);
        checkExistBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (approved == null) {
            throw new ValidationException("Нужно указать необходимый статус для бронирования");
        } else if (booking.getItem().getOwner().getId() != userId) {
            throw new UserNotFoundException("Статус бронирования может менять только владелец вещи");
        }

        if (approved) {
            if (booking.getStatusBooking().equals(StatusBooking.APPROVED)) {
                throw new ValidationException("Бронирование уже подтверждено");
            }
            booking.setStatusBooking(StatusBooking.APPROVED);
        } else {
            if (booking.getStatusBooking().equals(StatusBooking.REJECTED)) {
                throw new ValidationException("Бронирование уже отклонено");
            }
            booking.setStatusBooking(StatusBooking.REJECTED);
        }

        return bookingMapper.toDtoFromResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto findBooking(int userId, int bookingId) {
        checkExistBooking(bookingId);
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toDtoFromResponse(booking);
        } else {
            throw new UserNotFoundException("У вас нет бронирований с запрашиваемым предметом");
        }
    }

    @Override
    public List<BookingResponseDto> findBookingsByUser(int userId, String state) {
        checkUser(userId);
        List<Booking> booking = bookingRepository.findAllByBookerIdIs(userId);

        return sortedBookings(booking, state).stream()
                .map(bookingMapper::toDtoFromResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> findAllBookingsByItemsOwner(int userId, String state) {
        checkUser(userId);
        List<Booking> booking = bookingRepository.findAllBookingsByItemsOwner(userId);

        return sortedBookings(booking, state).stream()
                .map(bookingMapper::toDtoFromResponse)
                .collect(Collectors.toList());
    }

    private List<Booking> sortedBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL":
                return bookings.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> now.isAfter(b.getStart()) && now.isBefore(b.getEnd()))
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> now.isAfter(b.getEnd()))
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> now.isBefore(b.getStart()))
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatusBooking() == StatusBooking.WAITING)
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatusBooking() == StatusBooking.REJECTED)
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public void checkValidateBooking(BookingRequestDto bookingDto, int userId) {
        checkUser(userId);
        checkItem(bookingDto.getItemId());
        Integer ownerId = itemService.findItem(bookingDto.getItemId()).getOwner().getId();

        if (ownerId == userId) {
            throw new UserNotFoundException("Владелец не может забронировать собственную вещь");
        }

        if (!itemRepository.existsItemByIdAndAvailableIsTrue(bookingDto.getItemId())) {
            throw new ValidationException("Запрашиваемая вещь уже занята");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Не указан период аренды");
        }

        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), formatter);

        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Время начала использования не может быть позже или равен времени окончания");
        } else if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало использования не может быть в прошедшем времени");
        }
    }

    @Override
    public void checkExistBooking(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new UserNotFoundException("Бронирования с таким id не существует");
        }
    }

    private void checkUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }
    }

    private void checkItem(int itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Предмета с указанным Id не существует");
        }
    }
}