package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentListMapper.class, BookingMapper.class})
public interface ItemMapper {

    ItemDto toDto(Item item);

    Item toEntity(ItemDto itemDto);

    ItemForBooking toItemForBooking(Item item);

    ItemWithBookingsDto toItemWithBookingDto(Item item);
}
