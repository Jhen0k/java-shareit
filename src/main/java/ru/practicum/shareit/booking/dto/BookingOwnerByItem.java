package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingOwnerByItem {
    private Integer id;

    private String start;

    private String end;

    private Integer bookerId;
}
