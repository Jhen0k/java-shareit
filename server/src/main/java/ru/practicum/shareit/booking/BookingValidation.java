package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
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

    @Transactional
    public void checkValidateBooking(BookingRequestDto bookingDto, int userId) {
        userValidation.checkUser(userId);
        itemValidation.checkItem(itemRepository.findById(bookingDto.getItemId()));
        Integer ownerId = itemService.findItem(bookingDto.getItemId()).getOwner().getId();

        if (ownerId == userId) {
            log.error("Владелец не может забронировать собственную вещь");
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }

        if (!itemRepository.existsItemByIdAndAvailableIsTrue(bookingDto.getItemId())) {
            log.error("Запрашиваемая вещь уже занята");
            throw new ValidationException("Запрашиваемая вещь уже занята");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            log.error("Не указан период аренды");
            throw new ValidationException("Не указан период аренды");
        }

        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), formatter);

        if (start.isAfter(end) || start.equals(end)) {
            log.error("Время начала использования не может быть позже или равен времени окончания");
            throw new ValidationException("Время начала использования не может быть позже или равен времени окончания");
        }
        if (start.isBefore(LocalDateTime.now())) {
            log.error("Начало использования не может быть в прошедшем времени");
            throw new ValidationException("Начало использования не может быть в прошедшем времени");
        }
    }

    public void checkExistBooking(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.error("Бронирования с таким id не существует");
            throw new NotFoundException("Бронирования с таким id не существует");
        }
    }
}
