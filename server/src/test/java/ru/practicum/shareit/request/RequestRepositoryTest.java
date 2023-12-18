package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    @DisplayName("Поиск всех пользователей по запросу или id")
    void findAllByRequestor_IdIsTest() {
        User user = userRepository.save(new User(null, "mail@mail.ru", "name"));
        itemRepository.save(new Item(null, user, "name", "description", true));
        requestRepository.save(new ItemRequest(null, user, "description", LocalDateTime.now().minusDays(1)));
        requestRepository.save(new ItemRequest(null, user, "description", LocalDateTime.now()));

        List<ItemRequest> response = requestRepository.findAllByRequestor_IdIs(user.getId());

        assertEquals(response.size(), 2);
    }

    @Test
    @DisplayName("Поиск всех пользователей отсортированных в нужном порядке")
    void findAllBySortTest() {
        User user1 = userRepository.save(new User(null, "mail1@email", "name"));
        User user2 = userRepository.save(new User(null, "mail2@email", "name"));
        itemRepository.save(new Item(null, user1, "name", "description", true));
        ItemRequest itemRequest1 = requestRepository.save(new ItemRequest(null, user1, "description", LocalDateTime.now().minusDays(1)));
        ItemRequest itemRequest2 = requestRepository.save(new ItemRequest(null, user1, "description", LocalDateTime.now()));
        requestRepository.save(new ItemRequest(null, user2, "description", LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

        Page<ItemRequest> response = requestRepository.findAllNonOwnerBySort(user2.getId(), pageable);

        assertEquals(response.getTotalElements(), 2);
        assertEquals(response.toList().get(0), itemRequest2);
        assertEquals(response.toList().get(1), itemRequest1);
    }
}