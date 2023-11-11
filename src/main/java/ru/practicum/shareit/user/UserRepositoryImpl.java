package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final GenerateUserId generateUserId;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        Integer id = generateUserId.getId();
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User updateUser(Integer id, User user) {
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User findUser(Integer id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }
}
