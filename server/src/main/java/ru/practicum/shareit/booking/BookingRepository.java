package ru.practicum.shareit.booking;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdIs(Integer userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1")
    List<Booking> findAllBookingsByItemsOwner(Integer userId, Pageable pageable);

    List<Booking> findBookingsByItem(Item item);

    List<Booking> findBookingByItemIdAndBookerId(Integer itemId, Integer userId);
}
