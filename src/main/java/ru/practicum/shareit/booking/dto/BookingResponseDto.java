package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.user.dto.RentUserDto;

@Data
@AllArgsConstructor
public class BookingResponseDto {
    private Integer id;

    private String start;

    private String end;

    private RentUserDto booker;

    private ItemForBooking item;

    private StatusBooking status;
}