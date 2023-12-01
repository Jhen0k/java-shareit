package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemByOwnerId(int ownerId);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', '?1', '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            "and i.available is true")
    List<Item> searchByNameAndDescriptionAndAvailable(String text);

    boolean existsItemByIdAndAvailableIsTrue(int itemId);

    boolean existsItemByIdAndOwnerId(int itemId, int userId);

}