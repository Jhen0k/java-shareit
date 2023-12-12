package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;


    @Test
    void saveUser() {
        int userId = 1;
        UserDto userDto = new UserDto(userId, "join@mail.ru", "Jon");
        User userEntity = User.builder().id(userId).email("join@mail.ru").name("Jon").build();

        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        doNothing().when(userValidation).validateUser(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto responseUser = userService.createUser(userDto);

        assertThat(userDto, equalTo(responseUser));

        verify(userMapper, times(1)).toEntity(userDto);
        verifyNoMoreInteractions(userMapper);

        verify(userValidation, times(1)).validateUser(userEntity);
        verifyNoMoreInteractions(userValidation);

        verify(userRepository, times(1)).save(userEntity);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper, times(1)).toDto(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void updateUser() {
        int userId = 1;
        UserDto updateUser = UserDto.builder().id(userId).email("join@mail.ru").name("Jon").build();
        User oldUser = User.builder().id(userId).email("old@mail.ru").name("oldName").build();
        User saveUser = User.builder().id(userId).email("join@mail.ru").name("Jon").build();


        doNothing().when(userValidation).checkUser(userId);
        when(userMapper.toEntity(updateUser)).thenReturn(saveUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(saveUser)).thenReturn(saveUser);
        when(userMapper.toDto(saveUser)).thenReturn(updateUser);

        UserDto responseUser = userService.updateUser(userId, updateUser);

        assertThat(updateUser, equalTo(responseUser));

        verify(userValidation, times(1)).checkUser(userId);
        verifyNoMoreInteractions(userValidation);

        verify(userMapper, times(1)).toEntity(updateUser);
        verifyNoMoreInteractions(userMapper);

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verify(userRepository, times(1)).save(saveUser);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper, times(1)).toDto(saveUser);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void findUser() {
        int userId = 1;
        UserDto userDto = new UserDto(userId, "join@mail.ru", "Jon");
        User userEntity = User.builder().id(userId).email("join@mail.ru").name("Jon").build();

        doNothing().when(userValidation).checkUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto responseUser = userService.findUser(userId);


        assertThat(userDto, equalTo(responseUser));

        verify(userValidation, times(1)).checkUser(userId);
        verifyNoMoreInteractions(userValidation);

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper, times(1)).toDto(userEntity);
        verifyNoMoreInteractions(userMapper);

    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> responseUsers = userService.getAllUsers();

        assertThat(users.size(), equalTo(responseUsers.size()));

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser() {
        int userId = 1;

        doNothing().when(userValidation).checkUser(userId);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userValidation, times(1)).checkUser(userId);
        verifyNoMoreInteractions(userValidation);

        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}

