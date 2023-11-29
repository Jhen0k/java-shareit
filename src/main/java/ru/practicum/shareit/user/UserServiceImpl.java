package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    public UserDto createUser(UserDto user) {
        User userEntity = userMapper.toEntity(user);
        validateUser(userEntity);
        return userMapper.toDto(repository.save(userEntity));
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        checkUser(id);
        userDto.setId(id);
        User updatedUser = userMapper.toEntity(userDto);
        User oldUser = Optional.of(repository.findById(id)).get().orElseThrow();

        if (updatedUser.getName() != null) {
            oldUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            oldUser.setEmail(updatedUser.getEmail());
        }
        return userMapper.toDto(repository.save(oldUser));
    }

    @Override
    public UserDto findUser(Integer id) {
        checkUser(id);
        return userMapper.toDto(repository.getById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer id) {
        checkUser(id);
        repository.deleteById(id);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Имя не может быть пустым.");
        } else if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Электронная почта указана некорректно");
        }
    }

    private void checkUser(Integer id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
    }
}
