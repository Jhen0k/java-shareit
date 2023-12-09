package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserMapperTest {

    @Autowired
    private JacksonTester<UserDto> jsonDto;
    @Autowired
    private JacksonTester<User> jsonUser;

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1, "mail@mail.com", "jon");
        userDto = new UserDto(1, "mail@mail.com", "jon");
    }

    @Test
    void testUserDto() throws Exception{
        JsonContent<UserDto> result = jsonDto.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mail@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("jon");
    }

    @Test
    void testUser() throws Exception {
        JsonContent<User> result = jsonUser.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mail@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("jon");
    }
}

