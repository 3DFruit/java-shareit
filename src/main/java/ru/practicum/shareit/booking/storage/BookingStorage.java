package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker,
                                                              LocalDateTime localDateTime,
                                                              Pageable pageable);

    Page<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker,
                                                               LocalDateTime localDateTime,
                                                               Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findBookingByBookerAndDate(User booker,
                                             LocalDateTime localDateTime,
                                             Pageable pageable);

    Page<Booking> findAllByItemOwnerIsOrderByStartDesc(User owner, Pageable pageable);

    Page<Booking> findAllByItemOwnerIsAndStatusOrderByStartDesc(User owner,
                                                                BookingStatus status,
                                                                Pageable pageable);

    Page<Booking> findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(User owner,
                                                                   LocalDateTime localDateTime,
                                                                   Pageable pageable);

    Page<Booking> findAllByItemOwnerIsAndStartAfterOrderByStartDesc(User owner,
                                                                    LocalDateTime localDateTime,
                                                                    Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findBookingByOwnerAndDate(User owner, LocalDateTime localDateTime, Pageable pageable);

    Collection<Booking> findAllByBookerAndItemAndEndBeforeOrderByStartDesc(User booker,
                                                                           Item item,
                                                                           LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIsAndEndBeforeOrderByEndDesc(Item item, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIsAndStartAfterOrderByStartAsc(Item item, LocalDateTime localDateTime);
}
