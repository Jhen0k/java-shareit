package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRequestStorageImpl implements ItemRequestStorage {
    private final GenerateItemRequestId generateItemRequestId;
    private final Map<Integer, ItemRequest> itemRequests = new HashMap<>();

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        itemRequest.setId(generateItemRequestId.getId());
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest findItemRequest(int itemRequestId) {
        return itemRequests.get(itemRequestId);
    }

    @Override
    public ItemRequest updateItemRequest(ItemRequest itemRequest) {
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequests.get(itemRequest.getId());
    }

    @Override
    public void deleteItemRequest(int itemRequestId) {
        itemRequests.remove(itemRequestId);
    }
}
