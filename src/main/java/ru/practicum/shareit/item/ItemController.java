package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable int itemId) {
        ItemDto itemDto1 = itemService.updateItem(itemDto, userId, itemId);
        return itemDto1;
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto findItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllItemForOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAllItemForOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItem(@RequestParam String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.searchAvailableItem(text);
    }
}
