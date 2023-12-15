package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Создать запрос")
    void createRequest() throws Exception {
        int itemResponseId = 1;
        int userId = 1;
        ItemRequestDto itemRequestDto = new ItemRequestDto(0, "name", "description");
        ItemRequestDto itemRequestResponseDto = new ItemRequestDto(itemResponseId, "name", "description");

        when(requestService.createRequest(userId, itemRequestDto)).thenReturn(itemRequestResponseDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestResponseDto)));

        verify(requestService, times(1)).createRequest(userId, itemRequestDto);
    }

    @Test
    @DisplayName("Поиск всех запросов по пользователю")
    void findAllRequestForUserTest() throws Exception {
        int userId = 2;
        List<ItemRequestForResponseDto> responsesDto = new ArrayList<>();

        when(requestService.findAllRequestsByUser(userId)).thenReturn(responsesDto);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(content().json(objectMapper.writeValueAsString(responsesDto)));

        verify(requestService, times(1)).findAllRequestsByUser(userId);
    }

    @Test
    @DisplayName("Поиск всех запросов")
    void findAllRequestTest() throws Exception {
        int userId = 2;
        int from = 1;
        int size = 10;
        List<ItemRequestForResponseDto> responsesDto = new ArrayList<>();

        when(requestService.findAllRequests(userId, from, size)).thenReturn(responsesDto);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(content().json(objectMapper.writeValueAsString(responsesDto)));

        verify(requestService, times(1)).findAllRequests(userId, from, size);
    }

    @Test
    @DisplayName("Поиск запроса по id")
    void findRequestByIdTest() throws Exception {
        int itemRequestId = 1;
        int userId = 2;
        ItemRequestForResponseDto itemRequestForResponseDto = new ItemRequestForResponseDto();

        when(requestService.findRequestById(itemRequestId, userId)).thenReturn(itemRequestForResponseDto);

        mvc.perform(get("/requests/" + itemRequestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestForResponseDto)));

        verify(requestService, times(1)).findRequestById(itemRequestId, userId);
    }
}
