package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private int id;
    private final int userId;
    private final String name;
    private final String description;
}
