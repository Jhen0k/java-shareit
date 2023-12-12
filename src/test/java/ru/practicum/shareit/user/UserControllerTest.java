package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    UserDto userDto;

    List<UserDto> usersDto;

    int userId = 1;

    @BeforeEach
    void setUp() {

        userDto = new UserDto(
                1,
                "join@mail.ru",
                "Jon"
        );

        usersDto = new ArrayList<>();
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.createUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    void updateUser() throws Exception {
        UserDto updateUser = new UserDto(1, "update@mail.ru", "updateName");

        when(userService.updateUser(userId, userDto)).thenReturn(updateUser);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updateUser)));

        verify(userService, times(1)).updateUser(userId, userDto);
    }

    @Test
    void getAllUsers() throws Exception {
        usersDto.add(userDto);
        usersDto.add(userDto);

        when(userService.getAllUsers()).thenReturn(usersDto);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(userDto, userDto))));
    }

    @Test
    void getUserById() throws Exception {

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).findUser(userId);
    }

    @Test
    void deleteUser() throws Exception {

        mvc.perform(delete("/users/1")
                        .param("userId", "1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}

