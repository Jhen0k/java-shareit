package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;

@Service
public class GenerateItemRequestId {
    private Integer id = 1;

    public Integer getId() {
        return id++;
    }
}
