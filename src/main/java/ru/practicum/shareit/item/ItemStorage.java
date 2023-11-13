package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    List<Item> findAllItemForOwner(int userId);

    List<Item> searchAvailableItem(String text);

    void updateItem(Integer itemId, Item newItem);

    Item findItem(int itemId);
}
