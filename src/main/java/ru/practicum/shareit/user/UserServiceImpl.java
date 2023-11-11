package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UniqueEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto user) {
        User userEntity = userMapper.toEntity(user);
        validateUser(userEntity);
        validateEmail(userEntity);
        return userMapper.toDto(repository.save(userEntity));
    }

    @Override
    public UserDto updateUser(Integer id, UserDto user) {
        checkUser(id);
        user.setId(id);
        User updatedUser = userMapper.toEntity(user);
        User oldUser = repository.findUser(id);
        if (user.getEmail() != null && user.getName() != null) {
            return userMapper.toDto(repository.updateUser(id, updatedUser));
        } else if (user.getName() != null) {
            updatedUser.setName(user.getName());
            updatedUser.setEmail(oldUser.getEmail());
        } else if (user.getEmail() != null) {
            validateEmail(userMapper.toEntity(user));
            updatedUser.setName(oldUser.getName());
            updatedUser.setEmail(user.getEmail());
        }
        return userMapper.toDto(repository.updateUser(id, updatedUser));
    }

    @Override
    public UserDto findUser(Integer id) {
        return userMapper.toDto(repository.findUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer id) {
        checkUser(id);
        repository.deleteUser(id);
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

    private void validateEmail(User user) {
        List<User> users = repository.findAll();

        if (user.getId() != null) {
            users = users.stream().filter(user1 -> !Objects.equals(user1.getId(), user.getId()))
                    .collect(Collectors.toList());
        }

        List<String> emails = users.stream().map(User::getEmail).collect(Collectors.toList());

        if (emails.contains(user.getEmail())) {
            throw new UniqueEmailException("Пользователь с таким Email уже существует");
        }
    }

    @Override
    public void checkUser(Integer id) {
        User user = repository.findUser(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
    }
}