package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestValidation requestValidation;
    @Mock
    private UserValidation userValidation;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemService itemService;

    @Test
    @DisplayName("Создать запрос")
    void createRequest() {
        int userId = 0;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Description");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        doNothing().when(userValidation).checkUser(userId);
        doNothing().when(requestValidation).checkValidateResponse(itemRequestDto);
        when(itemRequestMapper.toEntity(itemRequestDto, userId)).thenReturn(itemRequest);
        when(requestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto itemRequestDto1 = requestService.createRequest(userId, itemRequestDto);

        assertEquals(itemRequestDto, itemRequestDto1);

        verify(userValidation).checkUser(userId);
        verify(itemRequestMapper).toEntity(itemRequestDto, userId);
        verify(itemRequestMapper).toRequestDto(itemRequest);
    }

    @Test
    @DisplayName("Поиск всех запросов по пользователю")
    void findAllRequestsByUserTest() {
        int userId = 1;
        List<ItemRequest> requests = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(userId);
        requests.add(itemRequest);
        ItemRequestForResponseDto requestForResponse = new ItemRequestForResponseDto();
        List<ItemForRequest> itemForRequests = new ArrayList<>();
        requestForResponse.setId(userId);

        doNothing().when(userValidation).checkUser(userId);
        when(requestRepository.findAllByRequestor_IdIs(userId)).thenReturn(requests);
        when(itemRequestMapper.toResponseDto(any(ItemRequest.class))).thenReturn(requestForResponse);
        when(itemService.findItemForRequest(userId)).thenReturn(itemForRequests);

        List<ItemRequestForResponseDto> itemRequestForResponseDto = requestService.findAllRequestsByUser(userId);
        assertEquals(itemRequestForResponseDto.get(0), requestForResponse);

        verify(userValidation).checkUser(userId);
        verify(requestRepository).findAllByRequestor_IdIs(userId);
        verify(itemRequestMapper).toResponseDto(any(ItemRequest.class));
    }

    @Test
    @DisplayName("Поиск всех запросов")
    void findAllRequestsTest() {
        int userId = 1;
        List<ItemRequest> itemRequests = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(userId);
        itemRequests.add(itemRequest);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests);
        ItemRequestForResponseDto itemRequestForResponseDto = new ItemRequestForResponseDto();
        itemRequestForResponseDto.setId(userId);
        List<ItemForRequest> itemForRequests = new ArrayList<>();

        when(requestRepository.findAllNonOwnerBySort(anyInt(), any(Pageable.class))).thenReturn(itemRequestPage);
        when(itemRequestMapper.toResponseDto(any(ItemRequest.class))).thenReturn(itemRequestForResponseDto);
        when(itemService.findItemForRequest(userId)).thenReturn(itemForRequests);

        List<ItemRequestForResponseDto> requestForResponseDto = requestService.findAllRequests(userId, 0, 1);
        assertEquals(requestForResponseDto.get(0), itemRequestForResponseDto);

        verify(userValidation).checkUser(userId);
        verify(requestRepository).findAllNonOwnerBySort(anyInt(), any(Pageable.class));
        verify(itemRequestMapper).toResponseDto(any(ItemRequest.class));
    }

    @Test
    @DisplayName("Поиск запроса по id")
    void findRequestById() {
        int requestId = 1;
        int userId = 1;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        ItemRequestForResponseDto itemRequestForResponseDto = new ItemRequestForResponseDto();
        itemRequestForResponseDto.setId(requestId);
        ItemRequestForResponseDto itemRequestForResponse = new ItemRequestForResponseDto();
        itemRequestForResponse.setId(requestId);

        when(requestRepository.existsById(requestId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toResponseDto(itemRequest)).thenReturn(itemRequestForResponse);

        ItemRequestForResponseDto request = requestService.findRequestById(requestId, userId);

        assertEquals(request, itemRequestForResponse);

        verify(requestRepository).findById(requestId);
        verify(userValidation).checkUser(userId);
    }
}