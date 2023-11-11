package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookingStorageImpl implements BookingStorage {
    private final GenerateBookingId generateBookingId;
    private final Map<Integer, Booking> bookings = new HashMap<>();

    @Override
    public Booking createBooking(Booking booking) {
        booking.setId(generateBookingId.getId());
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking findBooking(int bookingId) {
        return bookings.get(bookingId);
    }

    @Override
    public Booking updateBooking(Booking booking) {
        bookings.put(booking.getId(), booking);
        return bookings.get(booking.getId());
    }

    @Override
    public void deleteBooking(int bookingId) {
        bookings.remove(bookingId);
    }
}
