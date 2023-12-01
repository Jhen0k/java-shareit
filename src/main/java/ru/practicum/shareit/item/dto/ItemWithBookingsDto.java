package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ItemWithBookingsDto {

    private Integer id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;
    private List<CommentDto> comments;
    private BookingOwnerByItem lastBooking;
    private BookingOwnerByItem nextBooking;
}