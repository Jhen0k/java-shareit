package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {
    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;

    @Test
    void toEntity() {
        int userId = 1;
        ItemRequestDto itemRequestDto = new ItemRequestDto(0, "description", LocalDateTime.now().toString());

        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto, userId);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertNotNull(itemRequestDto.getCreated());
    }

    @Test
    void toResponseDto() {
        int id = 1;
        User requestor = User.builder().id(1).name("name").email("mail@mail.ru").build();
        String description = "description";
        LocalDateTime create = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(id, requestor, description, create);

        ItemRequestForResponseDto itemRequestForResponseDto = itemRequestMapper.toResponseDto(itemRequest);

        assertEquals(itemRequestForResponseDto.getId(), itemRequest.getId());
        assertEquals(itemRequestForResponseDto.getDescription(), itemRequest.getDescription());
        assertNotNull(itemRequestForResponseDto.getCreated());
    }

    @Test
    void toRequestDto() {
        int id = 1;
        User requestor = User.builder().id(1).name("name").email("mail@mail.ru").build();
        String description = "description";
        LocalDateTime create = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(id, requestor, description, create);

        ItemRequestDto itemRequestDto = itemRequestMapper.toRequestDto(itemRequest);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertNotNull(itemRequestDto.getCreated());
    }
}