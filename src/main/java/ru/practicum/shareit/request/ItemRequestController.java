package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestBody ItemRequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestForResponseDto> findAllRequestForUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestService.findAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestForResponseDto> findAllRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return requestService.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestForResponseDto findRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @PathVariable Integer requestId) {
        return requestService.findRequestById(requestId, userId);
    }
}
