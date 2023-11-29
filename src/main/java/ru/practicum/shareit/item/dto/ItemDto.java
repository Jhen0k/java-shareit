package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    private Integer ownerId;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;
    private List<CommentDto> comments;
}
