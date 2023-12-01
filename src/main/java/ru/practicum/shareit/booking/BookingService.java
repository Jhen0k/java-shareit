package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, int userId);

    BookingResponseDto updateStatusBooking(int userId, int bookingId, Boolean approved);

    void checkExistBooking(int bookingId);

    BookingResponseDto findBooking(int userId, int bookingId);

    List<BookingResponseDto> findBookingsByUser(int userId, String state);

    List<BookingResponseDto> findAllBookingsByItemsOwner(int userId, String state);

    void checkValidateBooking(BookingRequestDto bookingDto, int userId);
}