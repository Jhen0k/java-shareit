package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    List<Item> findAllItemForOwner(int userId);

    List<Item> searchAvailableItem(String text);

    void updateNameItem(int itemId, String name);

    void updateDescriptionItem(int itemId, String description);

    void updateAvailableItem(int itemId, boolean isAvailable);

    Item findItem(int itemId);
}
