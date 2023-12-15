package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @MockBean
    CommentService commentService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Создать вещь")
    void createItem() throws Exception {
        int itemId = 1;
        int userId = 2;
        ItemDto itemRequestDto = new ItemDto(null, "name", "description", true);
        ItemDto itemResponseDto = new ItemDto(itemId, "name", "description", true);

        when(itemService.createItem(itemRequestDto, userId)).thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseDto)));

        verify(itemService, times(1)).createItem(itemRequestDto, userId);
    }

    @Test
    @DisplayName("Обновить вещь")
    void updateItem() throws Exception {
        int itemId = 1;
        int userId = 2;
        ItemDto itemRequestDto = new ItemDto(itemId, "name", "description", true);
        ItemDto itemResponseDto = new ItemDto(itemId, "name", "description", true);

        when(itemService.updateItem(itemRequestDto, userId, itemId)).thenReturn(itemResponseDto);

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseDto)));

        verify(itemService, times(1)).updateItem(itemRequestDto, userId, itemId);
    }

    @Test
    @DisplayName("Поиск Вещи по id")
    void findItem() throws Exception {
        int itemId = 1;
        int userId = 2;
        User user = new User(userId, "mail@mail.ru", "name");
        UserDto userDto = new UserDto(userId, "mail@mail.ru", "name");
        BookingOwnerByItem bookingOwnerByItem = new BookingOwnerByItem(1, "2023-12-13T16:50:00", "2023-12-13T16:50:00", userId);
        ItemRequest itemRequest = new ItemRequest(itemId, user, "description", LocalDateTime.now());
        ItemWithBookingsDto itemResponseDto = new ItemWithBookingsDto(itemId, userDto, "name",
                "description", true, itemRequest, new ArrayList<>(), bookingOwnerByItem, bookingOwnerByItem);

        when(itemService.findItem(itemId, userId)).thenReturn(itemResponseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseDto)));

        verify(itemService, times(1)).findItem(itemId, userId);
    }

    @Test
    @DisplayName("Поиск всех вещей по хозяину")
    void findAllItemForOwner() throws Exception {
        int userId = 2;
        int from = 1;
        int size = 10;
        List<ItemWithBookingsDto> itemsWithBookingsResponseDto = new ArrayList<>();

        when(itemService.findAllItemForOwner(userId, from, size)).thenReturn(itemsWithBookingsResponseDto);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(content().json(objectMapper.writeValueAsString(itemsWithBookingsResponseDto)));

        verify(itemService, times(1)).findAllItemForOwner(userId, from, size);
    }

    @Test
    @DisplayName("Найти свободную вещь")
    void searchAvailableItem() throws Exception {
        String text = "search text";
        int from = 1;
        int size = 10;
        List<ItemDto> itemsResponseDto = new ArrayList<>();

        when(itemService.searchAvailableItem(text, from, size)).thenReturn(itemsResponseDto);

        mvc.perform(get("/items/search")
                        .param("text", "search text")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(content().json(objectMapper.writeValueAsString(itemsResponseDto)));

        verify(itemService, times(1)).searchAvailableItem(text, from, size);

        mvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<>())));
    }

    @Test
    @DisplayName("Создать комментарий")
    void createComment() throws Exception {
        int userId = 1;
        int itemId = 2;
        CommentDto commentRequest = new CommentDto(null, "text", "name", null);
        CommentDto commentResponse = new CommentDto(5, "text", "name", null);

        when(commentService.createComment(commentRequest, userId, itemId)).thenReturn(commentResponse);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponse)));

        verify(commentService, times(1)).createComment(commentRequest, userId, itemId);
    }
}
