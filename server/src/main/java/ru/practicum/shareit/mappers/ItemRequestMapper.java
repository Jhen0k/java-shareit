package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class ItemRequestMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ItemRequest toEntity(ItemRequestDto requestDto, Integer userId) {
        User user = new User();
        user.setId(userId);

        return new ItemRequest(
                0,
                user,
                requestDto.getDescription(),
                LocalDateTime.now()
        );
    }


    public ItemRequestForResponseDto toResponseDto(ItemRequest itemRequest) {
        ItemRequestForResponseDto responseDto = new ItemRequestForResponseDto();

        responseDto.setId(itemRequest.getId());
        responseDto.setDescription(itemRequest.getDescription());
        responseDto.setCreated(formatter.format(itemRequest.getCreated()));

        return responseDto;
    }

    public ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                formatter.format(itemRequest.getCreated()));
    }
}
