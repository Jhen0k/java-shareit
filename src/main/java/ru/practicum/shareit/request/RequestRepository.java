package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequestor_IdIs(int userId);

    @Query("select ir " +
            "from ItemRequest as ir " +
            "where ir.requestor.id != ?1")
    Page<ItemRequest> findAllNonOwnerBySort(int userId, Pageable pageable);
}
