package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Test
    @DisplayName("Поиск вещи по id владельца")
    void findItemByOwnerId() {
        User user1 = userRepository.save(new User(null, "mail@mail", "name"));
        itemRepository.save(new Item(null, user1, "name", "description", true));
        itemRepository.save(new Item(null, user1, "name second", "description second", true));
        User user2 = userRepository.save(new User(null, "new@mail", "name"));
        itemRepository.save(new Item(null, user2, "name second", "description second", true));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> findItem = itemRepository.findItemByOwnerId(user1.getId(), pageable);

        assertEquals(findItem.getTotalElements(), 2);
    }

    @Test
    @DisplayName("Найти по имени, описанию, свободная вещь")
    void searchByNameAndDescriptionAndAvailable() {
        User userFirst = userRepository.save(new User(null, "mail@mail", "name"));
        itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        userRepository.save(new User(null, "new@mail", "name"));
        itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));
        Pageable pageable = PageRequest.of(0, 10);
        String text = "вещ";

        Page<Item> findItem = itemRepository.searchByNameAndDescriptionAndAvailable(text, pageable);

        assertEquals(findItem.getTotalElements(), 1);
    }

    @Test
    @DisplayName("Проверить вещь по id, свободная и она существует")
    void existsItemByIdAndAvailableIsTrue_whenItemExistAndAvailableIsTrue_thenReturnTrue() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        itemRepository.save(new Item(null, userFirst, "name second", "description second", false));

        Boolean response = itemRepository.existsItemByIdAndAvailableIsTrue(itemFirst.getId());

        assertEquals(response, true);
    }

    @Test
    @DisplayName("Проверить вещь по id, свободная и она не существует")
    void existsItemByIdAndAvailableIsTrue_whenItemNonExist_thenReturnFalse() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        itemRepository.save(new Item(null, userFirst, "name second", "description second", false));

        Boolean response = itemRepository.existsItemByIdAndAvailableIsTrue(3);

        assertEquals(response, false);
    }

    @Test
    @DisplayName("Проверить вещь по id,вещь не свободная и она существует")
    void existsItemByIdAndAvailableIsTrue_whenItemExistAndAvailableIsFalse_thenReturnFalse() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", false));

        Boolean response = itemRepository.existsItemByIdAndAvailableIsTrue(itemSecond.getId());

        assertEquals(response, false);
    }

    @Test
    @DisplayName("Проверить вещь по id и id владельца")
    void existsItemByIdAndOwnerId_whenItemByOwner_thenReturnTrue() {
        User userFirst = userRepository.save(new User(null, "mail@mail", "name"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        userRepository.save(new User(null, "new@mail", "name"));
        itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));

        Boolean response = itemRepository.existsItemByIdAndOwnerId(itemFirst.getId(), userFirst.getId());

        assertEquals(response, true);
    }

    @Test
    @DisplayName("Проверить вещь по id и id владельца, возвращает false")
    void existsItemByIdAndOwnerId_whenItemNonByOwner_thenReturnFalse() {
        User userFirst = userRepository.save(new User(null, "mail@mail", "name"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        User userSecond = userRepository.save(new User(null, "new@mail", "name"));
        itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));

        Boolean response = itemRepository.existsItemByIdAndOwnerId(itemFirst.getId(), userSecond.getId());

        assertEquals(response, false);
    }

    @Test
    @DisplayName("Поиск всех вещей по id запроса")
    void findAllByItemRequest_id() {
        User userFirst = userRepository.save(new User(null, "mail@mail", "name"));
        userRepository.save(new User(null, "new@mail", "name"));
        ItemRequest itemRequest = requestRepository.save(new ItemRequest(null, userFirst, "description", null));
        itemRepository.save(new Item(null, userFirst, "вещь", "description", true, itemRequest));
        itemRepository.save(new Item(null, userFirst, "name second", "description second", true, itemRequest));
        itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));

        List<Item> response = itemRepository.findAllByItemRequest_Id(itemRequest.getId());

        assertEquals(response.size(), 2);
    }
}
