package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final UserValidation userValidation;


    @Transactional
    @Override
    public UserDto createUser(UserDto user) {
        User userEntity = userMapper.toEntity(user);
        userValidation.validateUser(userEntity);
        return userMapper.toDto(repository.save(userEntity));
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        userValidation.checkUser(id);
        userDto.setId(id);
        User updatedUser = userMapper.toEntity(userDto);
        User oldUser = repository.findById(id).orElseThrow();

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
        userValidation.checkUser(id);
        return userMapper.toDto(repository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer id) {
        userValidation.checkUser(id);
        repository.deleteById(id);
    }
}
