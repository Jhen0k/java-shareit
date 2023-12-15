package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findItemByOwnerId(int ownerId, Pageable pageable);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', '?1', '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            "and i.available is true")
    Page<Item> searchByNameAndDescriptionAndAvailable(String text, Pageable pageable);

    boolean existsItemByIdAndAvailableIsTrue(int itemId);

    boolean existsItemByIdAndOwnerId(int itemId, int userId);

    List<Item> findAllByItemRequest_Id(int requestId);

}
