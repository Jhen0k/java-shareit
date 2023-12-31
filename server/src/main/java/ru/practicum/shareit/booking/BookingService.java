package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, int userId);

    BookingResponseDto updateStatusBooking(int userId, int bookingId, Boolean approved);

    BookingResponseDto findBooking(int userId, int bookingId);

    List<BookingResponseDto> findBookingsByUser(int userId, String state, Integer from, Integer size);

    List<BookingResponseDto> findAllBookingsByItemsOwner(int userId, String state, Integer from, Integer size);
}