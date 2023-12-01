package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toEntity(BookingRequestDto bookingRequestDto);

    BookingResponseDto toDtoFromResponse(Booking booking);

    BookingOwnerByItem toBookingOwnerByItem(Booking booking);
}
