package ru.practicum.shareit.booking;

public interface BookingStorage {

    Booking createBooking(Booking booking);

    Booking findBooking(int bookingId);

    Booking updateBooking(Booking booking);

    void deleteBooking(int bookingId);
}
