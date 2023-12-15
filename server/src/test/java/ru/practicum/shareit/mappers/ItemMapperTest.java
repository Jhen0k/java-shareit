package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {
    @InjectMocks
    private ItemMapperImpl itemMapper;
    @Mock
    private UserMapperImpl userMapper;

    @Test
    void toDto() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        UserDto userDto = UserDto.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);

        when(userMapper.toDto(user)).thenReturn(userDto);

        ItemDto itemDto = itemMapper.toDto(item);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getOwner(), userDto);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toEntity() {
        int requestId = 1;
        List<CommentDto> commentDtoList = new ArrayList<>();
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        UserDto userDto = UserDto.builder().id(1).name("name").email("mail@mail.ru").build();
        ItemDto itemDto = new ItemDto(1, userDto, "name", "description", true, requestId, commentDtoList);

        when(userMapper.toEntity(userDto)).thenReturn(user);

        Item item = itemMapper.toEntity(itemDto);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getOwner(), user);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemForBooking() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);

        ItemForBooking itemForBooking = itemMapper.toItemForBooking(item);

        assertEquals(itemForBooking.getId(), item.getId());
        assertEquals(itemForBooking.getName(), item.getName());
    }

    @Test
    void toItemWithBookingDto() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);

        ItemWithBookingsDto itemWithBookingsDto = itemMapper.toItemWithBookingDto(item);

        assertEquals(itemWithBookingsDto.getId(), item.getId());
        assertEquals(item.getOwner(), user);
        assertEquals(itemWithBookingsDto.getName(), item.getName());
        assertEquals(itemWithBookingsDto.getDescription(), item.getDescription());
        assertEquals(itemWithBookingsDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItemForRequest() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);

        ItemForRequest itemForRequest = itemMapper.toItemForRequest(item);

        assertEquals(itemForRequest.getId(), item.getId());
        assertEquals(itemForRequest.getName(), item.getName());
        assertEquals(itemForRequest.getDescription(), item.getDescription());
    }
}