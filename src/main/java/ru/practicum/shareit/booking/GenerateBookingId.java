package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;

@Service
public class GenerateBookingId {
    private Integer id = 1;

    public Integer getId() {
        return id++;
    }
}
