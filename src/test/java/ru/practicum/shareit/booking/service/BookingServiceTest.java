package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.utils.exceptions.UnavailableItemException;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;
import ru.practicum.shareit.utils.service.ValidationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class BookingServiceTest {
    @Test
    void createBookingOwnerTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);
        Mockito
                .when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Assertions.assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(1L,
                new BookingPostDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(5))));
        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void createBookingItemNotAvailableTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                false,
                new User(2L, "test", "t@mail.com"),
                null);
        Mockito
                .when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Assertions.assertThrows(UnavailableItemException.class, () -> bookingService.createBooking(1L,
                new BookingPostDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(5))));
        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void createBookingTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                new User(2L, "test", "t@mail.com"),
                null);
        Mockito
                .when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(bookingStorage.save(any(Booking.class)))
                .thenReturn(new Booking(1L,
                        testItem,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(5),
                        testUser,
                        BookingStatus.WAITING));
        bookingService.createBooking(1L,
                new BookingPostDto(2L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(5)));
        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void patchBookingStatusWrongUserTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                new User(2L, "test", "t@mail.com"),
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testUser,
                BookingStatus.WAITING);
        Mockito
                .when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.patchBookingStatus(1L, 1L, true));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void patchBookingStatusWrongStatusTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                new User(1L, "test", "t@mail.com"),
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testUser,
                BookingStatus.REJECTED);
        Mockito
                .when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> bookingService.patchBookingStatus(1L, 1L, true));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void patchBookingStatusApprovedTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                new User(1L, "test", "t@mail.com"),
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testUser,
                BookingStatus.WAITING);
        Mockito
                .when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        Mockito
                .when(bookingStorage.save(any(Booking.class)))
                .then(org.mockito.AdditionalAnswers.returnsFirstArg());
        BookingDto answer = bookingService.patchBookingStatus(1L, 1L, true);
        Assertions.assertEquals(answer.getStatus(), BookingStatus.APPROVED);
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void patchBookingStatusRejectedTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                new User(1L, "test", "t@mail.com"),
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testUser,
                BookingStatus.WAITING);
        Mockito
                .when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        Mockito
                .when(bookingStorage.save(any(Booking.class)))
                .then(org.mockito.AdditionalAnswers.returnsFirstArg());
        BookingDto answer = bookingService.patchBookingStatus(1L, 1L, false);
        Assertions.assertEquals(answer.getStatus(), BookingStatus.REJECTED);
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .save(any(Booking.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void getBookingWrongUserTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testOwner = new User(1L, "test name", "test@mail.com");
        User testBooker = new User(2L, "test booker", "booker@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                testOwner,
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testBooker,
                BookingStatus.WAITING);
        Mockito
                .when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBooking(3L, 1L));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void getBookingOwnerOrBookerTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testOwner = new User(1L, "test name", "test@mail.com");
        User testBooker = new User(2L, "test booker", "booker@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                testOwner,
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testBooker,
                BookingStatus.WAITING);
        Mockito
                .when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        Assertions.assertDoesNotThrow(() -> bookingService.getBooking(testOwner.getId(), 1L));
        Assertions.assertDoesNotThrow(() -> bookingService.getBooking(testBooker.getId(), 1L));
        Mockito
                .verify(bookingStorage, Mockito.times(2))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void getBookingsOfUserTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testOwner = new User(1L, "test name", "test@mail.com");
        User testBooker = new User(2L, "test booker", "booker@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                testOwner,
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testBooker,
                BookingStatus.WAITING);
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        Mockito
                .when(bookingStorage.findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findBookingByBookerAndDate(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findAllByBookerAndEndBeforeOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findAllByBookerAndStartAfterOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findAllByBookerAndStatusOrderByStartDesc(any(User.class),
                        any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        bookingService.getBookingsOfUser(2L, BookingState.ALL, 0, 20);
        bookingService.getBookingsOfUser(2L, BookingState.CURRENT, 0, 20);
        bookingService.getBookingsOfUser(2L, BookingState.PAST, 0, 20);
        bookingService.getBookingsOfUser(2L, BookingState.FUTURE, 0, 20);
        bookingService.getBookingsOfUser(2L, BookingState.WAITING, 0, 20);
        bookingService.getBookingsOfUser(2L, BookingState.REJECTED, 0, 20);
        Mockito
                .verify(userStorage, Mockito.times(6))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findBookingByBookerAndDate(any(User.class), any(LocalDateTime.class), any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByBookerAndEndBeforeOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByBookerAndStartAfterOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(2))
                .findAllByBookerAndStatusOrderByStartDesc(any(User.class),
                        any(BookingStatus.class),
                        any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void getBookingsOfUserItemsTest() {
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ValidationService validationService = Mockito.mock(ValidationService.class);
        BookingService bookingService = new BookingServiceImpl(bookingStorage,
                userStorage,
                itemStorage,
                validationService);
        User testOwner = new User(1L, "test name", "test@mail.com");
        User testBooker = new User(2L, "test booker", "booker@mail.com");
        Item testItem = new Item(1L,
                "test",
                "description",
                true,
                testOwner,
                null);
        Booking testBooking = new Booking(1L,
                testItem,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(5),
                testBooker,
                BookingStatus.WAITING);
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIsOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findBookingByOwnerAndDate(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIsAndStartAfterOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIsAndStatusOrderByStartDesc(any(User.class),
                        any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        bookingService.getBookingsOfUserItems(1L, BookingState.ALL, 0, 20);
        bookingService.getBookingsOfUserItems(1L, BookingState.CURRENT, 0, 20);
        bookingService.getBookingsOfUserItems(1L, BookingState.PAST, 0, 20);
        bookingService.getBookingsOfUserItems(1L, BookingState.FUTURE, 0, 20);
        bookingService.getBookingsOfUserItems(1L, BookingState.WAITING, 0, 20);
        bookingService.getBookingsOfUserItems(1L, BookingState.REJECTED, 0, 20);
        Mockito
                .verify(userStorage, Mockito.times(6))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIsOrderByStartDesc(any(User.class), any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findBookingByOwnerAndDate(any(User.class), any(LocalDateTime.class), any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIsAndStartAfterOrderByStartDesc(any(User.class),
                        any(LocalDateTime.class),
                        any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(2))
                .findAllByItemOwnerIsAndStatusOrderByStartDesc(any(User.class),
                        any(BookingStatus.class),
                        any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }
}
