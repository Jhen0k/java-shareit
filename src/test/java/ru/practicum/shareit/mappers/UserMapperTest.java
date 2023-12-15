package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.RentUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    UserMapperImpl userMapper;

    @Test
    void toDto() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();

        UserDto userDto = userMapper.toDto(user);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void toEntity() {
        UserDto userDto = UserDto.builder().id(1).name("name").email("mail@mail.ru").build();

        User user = userMapper.toEntity(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toRentUserDto() {
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();

        RentUserDto rentUserDto = userMapper.toRentUserDto(user);

        assertEquals(rentUserDto.getId(), user.getId());
        assertEquals(rentUserDto.getName(), user.getName());
    }
}

