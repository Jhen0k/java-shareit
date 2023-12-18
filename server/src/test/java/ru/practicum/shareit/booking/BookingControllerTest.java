package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.user.dto.RentUserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private final String start = "2023-12-13T16:18:00";
    private final String end = "2023-12-13T16:18:00";

    @Test
    @DisplayName("Создать бронирование")
    void createBooking() throws Exception {
        int userId = 1;
        int itemId = 2;
        RentUserDto user = new RentUserDto(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5, start, end, user, item, StatusBooking.APPROVED);

        when(bookingService.createBooking(bookingRequestDto, userId)).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @Test
    @DisplayName("Обновить бронирование")
    void updateStatusBooking() throws Exception {
        int userId = 1;
        int itemId = 2;
        int bookingId = 5;
        boolean isApproved = true;
        RentUserDto user = new RentUserDto(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5, start, end, user, item, StatusBooking.APPROVED);

        when(bookingService.updateStatusBooking(userId, bookingId, isApproved)).thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/5")
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @Test
    @DisplayName("Поиск бронирования по id")
    void findBooking() throws Exception {
        int userId = 1;
        int itemId = 2;
        int bookingId = 5;
        RentUserDto user = new RentUserDto(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5, start, end, user, item, StatusBooking.APPROVED);

        when(bookingService.findBooking(userId, bookingId)).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/5")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @Test
    @DisplayName("Поиск бронирования по пользователю")
    void findBookingsByUser() throws Exception {
        int userId = 1;
        int itemId = 2;
        String state = "ALL";
        int from = 0;
        int size = 10;
        RentUserDto user = new RentUserDto(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5, start, end, user, item, StatusBooking.APPROVED);
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        when(bookingService.findBookingsByUser(userId, state, from, size)).thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }

    @Test
    @DisplayName("Поиск всех бронирований по вещам хозяина")
    void findAllBookingsByItemsOwner() throws Exception {
        int userId = 1;
        int itemId = 2;
        String state = "ALL";
        int from = 0;
        int size = 10;
        RentUserDto user = new RentUserDto(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5, start, end, user, item, StatusBooking.APPROVED);
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        when(bookingService.findAllBookingsByItemsOwner(userId, state, from, size)).thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }
}