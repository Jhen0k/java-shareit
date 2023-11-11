package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

@Service
public class GenerateUserId {
    private Integer id = 1;

    public Integer getId() {
        return id++;
    }
}
