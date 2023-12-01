package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, int userId, int itemId);

    ItemWithBookingsDto findItem(int itemId);

    ItemWithBookingsDto findItem(int itemId, int userId);

    List<ItemDto> findAllItemForOwner(int userId);

    List<ItemDto> searchAvailableItem(String text);
}
