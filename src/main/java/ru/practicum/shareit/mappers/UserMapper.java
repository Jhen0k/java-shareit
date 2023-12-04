package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.RentUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    RentUserDto toRentUserDto(User user);
}
