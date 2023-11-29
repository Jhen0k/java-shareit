package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class BookingOwnerByItem {
    private Integer id;

    private String start;

    private String end;

    private Integer bookerId;

    public BookingOwnerByItem(Integer id, String start, String end, Integer bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.bookerId = bookerId;
    }
}
