package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemForRequest {
    private Integer id;
    private String name;
    private String description;
    private Integer requestId;
    private boolean available;
}
