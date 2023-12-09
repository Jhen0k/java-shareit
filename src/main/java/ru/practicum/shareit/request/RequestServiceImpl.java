package ru.practicum.shareit.request;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserValidation;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class RequestServiceImpl implements RequestService {
    private final RequestValidation requestValidation;
    private final UserValidation userValidation;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final RequestRepository requestRepository;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto createRequest(int userId, ItemRequestDto requestDto) {
        userValidation.checkUser(userId);
        requestValidation.checkValidateResponse(requestDto);

        ItemRequest itemRequest = itemRequestMapper.toEntity(requestDto, userId);

        return itemRequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional
    public List<ItemRequestForResponseDto> findAllRequestsByUser(int userId) {
        userValidation.checkUser(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdIs(userId);
        List<ItemRequestForResponseDto> requestForResponse = requests.stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
        addItemResponse(requestForResponse);

        return requestForResponse;
    }

    @Transactional
    @Override
    public List<ItemRequestForResponseDto> findAllRequests(int userId, int from, int size) {
        Pageable pageable = Paginator.getPageable(from, size, "created");
        userValidation.checkUser(userId);

        Page<ItemRequest> itemRequests = requestRepository.findAllNonOwnerBySort(userId, pageable);
        List<ItemRequestForResponseDto> requestsDto = itemRequests.stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
        addItemResponse(requestsDto);

        return requestsDto;
    }

    @Transactional
    @Override
    public ItemRequestForResponseDto findRequestById(int requestId, int userId) {
        userValidation.checkUser(userId);
        checkExistRequest(requestId);

        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow();
        ItemRequestForResponseDto responseDto = itemRequestMapper.toResponseDto(itemRequest);
        addItemResponse(responseDto);

        return responseDto;
    }

    public void checkExistRequest(int id) {
        if (!requestRepository.existsById(id)) throw new NotFoundException("Запроса с таким id не существует");
    }

    private void addItemResponse(ItemRequestForResponseDto responseDto) {
        int requestId = responseDto.getId();
        responseDto.setItems(itemService.findItemForRequest(requestId));
    }

    public void addItemResponse(List<ItemRequestForResponseDto> responsesDtoList) {
        responsesDtoList.forEach(itemRequestDtForResponseDto -> {
            int requestId = itemRequestDtForResponseDto.getId();
            itemRequestDtForResponseDto.setItems(itemService.findItemForRequest(requestId));
        });
    }

}
