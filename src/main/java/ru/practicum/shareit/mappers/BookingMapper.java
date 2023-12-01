package ru.practicum.shareit.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class BookingMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public Booking toEntity(BookingRequestDto bookingRequestDto, User user, Item item){
        return new Booking(0,
                LocalDateTime.parse(bookingRequestDto.getStart(), formatter),
                LocalDateTime.parse(bookingRequestDto.getEnd(), formatter),
                user,
                item,
                StatusBooking.WAITING);
    };

    public BookingResponseDto toDtoFromResponse(Booking booking){
        return new BookingResponseDto(booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                userMapper.toRentUserDto(booking.getBooker()),
                itemMapper.toItemForBooking(booking.getItem()),
                booking.getStatusBooking());
    };

    public BookingOwnerByItem toBookingOwnerByItem(Booking booking){
        return new BookingOwnerByItem(booking.getId(),
                formatter.format(booking.getStart()),
                formatter.format(booking.getEnd()),
                booking.getBooker().getId());
    };
}
