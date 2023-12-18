package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, int userId, int itemId);

    ItemWithBookingsDto findItem(int itemId);

    ItemWithBookingsDto findItem(int itemId, int userId);

    List<ItemWithBookingsDto> findAllItemForOwner(int userId, Integer from, Integer size);

    List<ItemDto> searchAvailableItem(String text, Integer from, Integer size);

    List<ItemForRequest> findItemForRequest(int requestId);
}
