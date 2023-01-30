package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerOrderByStartDesc(User booker);

    Collection<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    Collection<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime localDateTime);

    Collection<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc")
    Collection<Booking> findBookingByBookerAndDate(User booker, LocalDateTime localDateTime);

    Collection<Booking> findAllByItemOwnerIsOrderByStartDesc(User owner);

    Collection<Booking> findAllByItemOwnerIsAndStatusOrderByStartDesc(User owner, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(User owner, LocalDateTime localDateTime);

    Collection<Booking> findAllByItemOwnerIsAndStartAfterOrderByStartDesc(User owner, LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc")
    Collection<Booking> findBookingByOwnerAndDate(User booker, LocalDateTime localDateTime);

    Collection<Booking> findAllByBookerAndItemAndEndBeforeOrderByStartDesc(User booker,
                                                                           Item item,
                                                                           LocalDateTime localDateTime);

    Booking findAllByItemInAndEndBeforeOrderByStartDesc(Collection<Item> items, LocalDateTime localDateTime);

    Booking findNextBooking(Item item);
}
