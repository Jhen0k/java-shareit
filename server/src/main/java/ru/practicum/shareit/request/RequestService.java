package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto createRequest(int userId, ItemRequestDto requestDto);

    List<ItemRequestForResponseDto> findAllRequestsByUser(int userId);

    ItemRequestForResponseDto findRequestById(int requestId, int userId);

    List<ItemRequestForResponseDto> findAllRequests(int userId, int from, int size);
}
