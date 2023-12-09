package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void saveUser() {
        UserDto userDto = makeUserDto("some@email.com", "Сергей");
        userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getAllUsers() {
        UserDto userDto = makeUserDto("some@email.com", "Валера");
        userService.createUser(userDto);

        List<UserDto> usersDto = userService.getAllUsers();
        UserDto user2 = usersDto.get(0);

        assertThat(usersDto, hasItem(isA(UserDto.class)));
        assertThat(1, equalTo(usersDto.size()));
        assertThat(user2.getId(), notNullValue());
        assertThat(user2.getName(), equalTo(userDto.getName()));
        assertThat(user2.getEmail(), equalTo(userDto.getEmail()));
    }

    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }
}

