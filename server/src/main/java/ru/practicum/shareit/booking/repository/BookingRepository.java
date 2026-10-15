package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Вызовы бд через бронирующего

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(Long id);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdPast(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdFuture(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdCurrent(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdStatus(Long id, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.status = ?3 AND b.end < ?4")
    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId, BookingStatus status, LocalDateTime localDateTime);


    // Вызовы бд через владельца вещи

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(Long id);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdPast(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdFuture(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdCurrent(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdStatus(Long id, BookingStatus bookingStatus);

    // вызовы бд для сортировок по датам бронирования

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.status = ?2 AND b.start < ?3 ORDER BY b.start DESC")
    List<Booking> findLastBookingsByOwnerId(Long ownerId, BookingStatus status, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.status = ?2 AND b.start > ?3 ORDER BY b.start ASC")
    List<Booking> findNextBookingsByOwnerId(Long ownerId, BookingStatus status, LocalDateTime localDateTime);

}
