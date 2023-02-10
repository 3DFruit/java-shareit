package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

@DataJpaTest
public class BookingStorageTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingStorage bookingStorage;
    private final User booker = new User();
    private final User owner = new User();
    private final Item testItem = new Item();

    @BeforeEach
    public void populateData() {
        owner.setName("owner");
        owner.setEmail("owner@mail.com");
        em.persist(owner);
        booker.setName("booker");
        booker.setEmail("booker@mail.com");
        em.persist(booker);
        testItem.setName("item1");
        testItem.setDescription("New description");
        testItem.setAvailable(true);
        testItem.setOwner(owner);
        em.persist(testItem);
        LocalDateTime minDate = LocalDateTime.now().plusDays(1);
        LocalDateTime maxDate = LocalDateTime.now().plusDays(31);
        for (int i = 0; i < 100; i++) {
            Booking booking = new Booking();
            booking.setItem(testItem);
            booking.setBooker(booker);
            booking.setStatus(BookingStatus.values()
                    [ThreadLocalRandom.current().nextInt(0, 4)]);
            LocalDateTime randomDate = LocalDateTime.ofEpochSecond(
                    ThreadLocalRandom.current().nextLong(
                            minDate.toEpochSecond(ZoneOffset.ofHours(0)),
                            maxDate.toEpochSecond(ZoneOffset.ofHours(0))
                    ),
                    0,
                    ZoneOffset.ofHours(0)
            );
            booking.setStart(randomDate);
            booking.setEnd(randomDate.plusDays(
                    ThreadLocalRandom.current().nextInt(1, 15)
            ));
            em.persist(booking);
        }
    }

    @Test
    void findAllByBookerTest() {
        Page<Booking> bookings = bookingStorage.findAllByBookerOrderByStartDesc(
                booker, PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getBooker().getId().equals(booker.getId())));
    }

    @Test
    void findAllByBookerAndStatusTest() {
        Page<Booking> bookings = bookingStorage.findAllByBookerAndStatusOrderByStartDesc(
                booker, BookingStatus.WAITING, PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getStatus().equals(BookingStatus.WAITING)
                        && booking.getBooker().getId().equals(booker.getId())));
    }

    @Test
    void findAllByBookerAndEndBeforeTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Page<Booking> bookings = bookingStorage.findAllByBookerAndEndBeforeOrderByStartDesc(
                booker,
                dateTime,
                PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getEnd().isBefore(dateTime)
                        && booking.getBooker().getId().equals(booker.getId())));
    }

    @Test
    void findAllByBookerAndStartAfterTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Page<Booking> bookings = bookingStorage.findAllByBookerAndStartAfterOrderByStartDesc(
                booker,
                dateTime,
                PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getStart().isAfter(dateTime)
                        && booking.getBooker().getId().equals(booker.getId())));
    }

    @Test
    void findByBookerAndDateTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Page<Booking> bookings = bookingStorage.findBookingByBookerAndDate(
                booker,
                dateTime,
                PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getStart().isBefore(dateTime)
                        && booking.getEnd().isAfter(dateTime)
                        && booking.getBooker().getId().equals(booker.getId())));
    }

    @Test
    void findAllByOwnerTest() {
        Page<Booking> bookings = bookingStorage.findAllByItemOwnerIsOrderByStartDesc(
                owner, PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getItem().getOwner().getId().equals(owner.getId())));
    }

    @Test
    void findAllByOwnerAndStatusTest() {
        Page<Booking> bookings = bookingStorage.findAllByItemOwnerIsAndStatusOrderByStartDesc(
                owner, BookingStatus.WAITING, PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getStatus().equals(BookingStatus.WAITING)
                        && booking.getItem().getOwner().getId().equals(owner.getId())));
    }

    @Test
    void findAllByOwnerAndEndBeforeTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Page<Booking> bookings = bookingStorage.findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(
                owner,
                dateTime,
                PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getEnd().isBefore(dateTime)
                        && booking.getItem().getOwner().getId().equals(owner.getId())));
    }

    @Test
    void findAllByOwnerAndStartAfterTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Page<Booking> bookings = bookingStorage.findAllByItemOwnerIsAndStartAfterOrderByStartDesc(
                owner,
                dateTime,
                PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getStart().isAfter(dateTime)
                        && booking.getItem().getOwner().getId().equals(owner.getId())));
    }

    @Test
    void findByOwnerAndDateTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Page<Booking> bookings = bookingStorage.findBookingByOwnerAndDate(
                owner,
                dateTime,
                PageRequest.of(0, 20));
        Assertions.assertNotEquals(0, bookings.getTotalElements());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getStart().isBefore(dateTime)
                        && booking.getEnd().isAfter(dateTime)
                        && booking.getItem().getOwner().getId().equals(owner.getId())));
    }

    @Test
    void findByBookerAndItemAndEndBeforeTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Collection<Booking> bookings = bookingStorage.findAllByBookerAndItemAndEndBeforeOrderByStartDesc(
                booker,
                testItem,
                dateTime);
        Assertions.assertNotEquals(0, bookings.size());
        Assertions.assertTrue(bookings.stream().allMatch(
                (Booking booking) -> booking.getEnd().isBefore(dateTime)
                        && booking.getItem().getId().equals(testItem.getId())
                        && booking.getBooker().getId().equals(booker.getId())));
    }

    @Test
    void findLastBookingTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Booking booking = bookingStorage.findFirstByItemIsAndEndBeforeOrderByEndDesc(
                testItem,
                dateTime).orElse(null);
        Assertions.assertNotNull(booking);
        Booking lastBooking = bookingStorage.findAll().stream()
                        .filter((Booking b) -> b.getItem().getId().equals(testItem.getId()))
                                .filter((Booking b) -> b.getEnd().isBefore(dateTime))
                .max(Comparator.comparing(Booking::getEnd))
                .get();
        Assertions.assertEquals(booking.getId(), lastBooking.getId());
    }

    @Test
    void findNextBookingTest() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(15);
        Booking booking = bookingStorage.findFirstByItemIsAndStartAfterOrderByStartAsc(
                testItem,
                dateTime).orElse(null);
        Assertions.assertNotNull(booking);
        Booking nextBooking = bookingStorage.findAll().stream()
                .filter((Booking b) -> b.getItem().getId().equals(testItem.getId()))
                .filter((Booking b) -> b.getStart().isAfter(dateTime) || b.getStart().isEqual(dateTime))
                .min(Comparator.comparing(Booking::getStart))
                .get();
        Assertions.assertEquals(booking.getId(), nextBooking.getId());
    }
}
