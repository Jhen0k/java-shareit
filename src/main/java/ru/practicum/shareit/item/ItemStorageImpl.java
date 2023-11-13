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
    private int id = 1;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(id++);
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
    public void updateItem(Integer itemId, Item newItem) {
        Item item = items.get(itemId);

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
            items.put(itemId, item);
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
            items.put(itemId, item);
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
            items.put(itemId, item);
        }
    }

    @Override
    public Item findItem(int itemId) {
        return items.get(itemId);
    }
}
