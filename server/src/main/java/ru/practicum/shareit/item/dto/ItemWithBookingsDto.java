package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Data
@AllArgsConstructor
public class ItemWithBookingsDto {

    private Integer id;
    private UserDto owner;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;
    private List<CommentDto> comments;
    private BookingOwnerByItem lastBooking;
    private BookingOwnerByItem nextBooking;
}