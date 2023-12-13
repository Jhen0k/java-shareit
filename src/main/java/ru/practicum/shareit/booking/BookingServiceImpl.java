package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidation;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingValidation bookingValidation;

    private final UserValidation userValidation;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;


    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, int userId) {
        bookingValidation.checkValidateBooking(bookingRequestDto, userId);
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow();
        Booking booking = bookingMapper.toEntity(bookingRequestDto, user, item);
        return bookingMapper.toDtoFromResponse(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto updateStatusBooking(int userId, int bookingId, Boolean approved) {
        userValidation.checkUser(userId);
        bookingValidation.checkExistBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (approved == null) {
            log.error("Нужно указать необходимый статус для бронирования");
            throw new ValidationException("Нужно указать необходимый статус для бронирования");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            log.error("Статус бронирования может менять только владелец вещи");
            throw new UserNotFoundException("Статус бронирования может менять только владелец вещи");
        }

        if (approved) {
            if (booking.getStatusBooking().equals(StatusBooking.APPROVED)) {
                log.error("Бронирование уже подтверждено");
                throw new ValidationException("Бронирование уже подтверждено");
            }
            booking.setStatusBooking(StatusBooking.APPROVED);
        } else {
            if (booking.getStatusBooking().equals(StatusBooking.REJECTED)) {
                log.error("Бронирование уже отклонено");
                throw new ValidationException("Бронирование уже отклонено");
            }
            booking.setStatusBooking(StatusBooking.REJECTED);
        }

        return bookingMapper.toDtoFromResponse(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto findBooking(int userId, int bookingId) {
        bookingValidation.checkExistBooking(bookingId);
        userValidation.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toDtoFromResponse(booking);
        } else {
            log.error("У вас нет бронирований с запрашиваемым предметом");
            throw new UserNotFoundException("У вас нет бронирований с запрашиваемым предметом");
        }
    }

    @Transactional
    @Override
    public List<BookingResponseDto> findBookingsByUser(int userId, String state, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size, "end");
        userValidation.checkUser(userId);
        List<Booking> booking = bookingRepository.findAllByBookerIdIs(userId, pageable);

        return sortedBookings(booking, state.toUpperCase()).stream().map(bookingMapper::toDtoFromResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingResponseDto> findAllBookingsByItemsOwner(int userId, String state, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size, "end");
        userValidation.checkUser(userId);
        List<Booking> booking = bookingRepository.findAllBookingsByItemsOwner(userId, pageable);

        return sortedBookings(booking, state.toUpperCase()).stream().map(bookingMapper::toDtoFromResponse).collect(Collectors.toList());
    }

    private List<Booking> sortedBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL":
                break;
            case "CURRENT":
                bookings = bookings.stream().filter(b -> now.isAfter(b.getStart()) && now.isBefore(b.getEnd())).collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookings.stream().filter(b -> now.isAfter(b.getEnd())).collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookings.stream().filter(b -> now.isBefore(b.getStart())).collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookings.stream().filter(b -> b.getStatusBooking() == StatusBooking.WAITING).collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookings.stream().filter(b -> b.getStatusBooking() == StatusBooking.REJECTED).collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream().sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
    }
}
