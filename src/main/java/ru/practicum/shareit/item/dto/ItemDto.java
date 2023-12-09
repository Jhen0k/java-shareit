package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    private UserDto owner;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private List<CommentDto> comments;
}
