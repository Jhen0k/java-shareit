package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, int userId);

    BookingResponseDto updateStatusBooking(Integer userId, Integer bookingId, Boolean approved);

    void checkExistBooking(Integer bookingId);

    BookingResponseDto findBooking(Integer userId, Integer bookingId);

    List<BookingResponseDto> findBookingsByUser(Integer userId, String state);

    List<BookingResponseDto> findAllBookingsByItemsOwner(Integer userId, String state);

    void checkValidateBooking(BookingRequestDto bookingDto, int userId);
}