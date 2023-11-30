package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Getter
@Setter
public class ItemWithBookingsDto extends ItemDto {

    private BookingOwnerByItem lastBooking;

    private BookingOwnerByItem nextBooking;

    public ItemWithBookingsDto(Integer id,
                               User ownerId,
                               String name,
                               String description,
                               Boolean available,
                               ItemRequest itemRequest,
                               List<CommentDto> commentsDto) {
        super(id, ownerId, name, description, available, itemRequest, commentsDto);
    }
}