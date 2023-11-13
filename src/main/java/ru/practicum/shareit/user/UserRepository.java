package ru.practicum.shareit.user;

import java.util.List;

interface UserRepository {
    User save(User user);

    User updateUser(Integer id, User user);

    User findUser(Integer id);

    List<User> findAll();

    void deleteUser(Integer id);
}
