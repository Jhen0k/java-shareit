package ru.practicum.shareit.request;

public interface ItemRequestStorage {

    ItemRequest createItemRequest(ItemRequest itemRequest);

    ItemRequest findItemRequest(int itemRequestId);

    ItemRequest updateItemRequest(ItemRequest itemRequest);

    void deleteItemRequest(int itemRequestId);
}
