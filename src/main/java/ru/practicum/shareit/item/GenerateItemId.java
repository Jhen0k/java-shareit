package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

@Service
public class GenerateItemId {
    private Integer id = 1;

    public Integer getId() {
        return id++;
    }
}
