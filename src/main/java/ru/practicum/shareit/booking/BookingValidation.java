package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemValidation;
import ru.practicum.shareit.user.UserValidation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@AllArgsConstructor
public class BookingValidation {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final UserValidation userValidation;
    private final ItemValidation itemValidation;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public void checkValidateBooking(BookingRequestDto bookingDto, int userId) {
        userValidation.checkUser(userId);
        itemValidation.checkItem(itemRepository.findById(bookingDto.getItemId()));
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
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало использования не может быть в прошедшем времени");
        }
    }

    public void checkExistBooking(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new UserNotFoundException("Бронирования с таким id не существует");
        }
    }
}