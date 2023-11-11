package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final GenerateItemId generateItemId;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(generateItemId.getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findAllItemForOwner(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchAvailableItem(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }

    @Override
    public void updateNameItem(int itemId, String name) {
        Item item = items.get(itemId);
        item.setName(name);
        items.put(itemId, item);
    }

    @Override
    public void updateDescriptionItem(int itemId, String description) {
        Item item = items.get(itemId);
        item.setDescription(description);
        items.put(itemId, item);
    }

    @Override
    public void updateAvailableItem(int itemId, boolean isAvailable) {
        Item item = items.get(itemId);
        item.setAvailable(isAvailable);
        items.put(itemId, item);
    }

    @Override
    public Item findItem(int itemId) {
        return items.get(itemId);
    }
}
