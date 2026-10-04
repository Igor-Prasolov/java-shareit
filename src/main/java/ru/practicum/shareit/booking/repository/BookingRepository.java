package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;


import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(Long id);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(Long id);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.item.id = ?2")
    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

}
