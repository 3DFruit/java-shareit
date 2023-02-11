package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.UnauthorizedAccessException;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class ItemServiceTest {
    @Test
    void addItemTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemStorage.save(any(Item.class)))
                .thenReturn(testItem);

        itemService.addItem(1L, new ItemDto(1L,
                "test",
                "description",
                true,
                null,
                null,
                null,
                null));

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .save(any(Item.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void addItemWithRequestTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemStorage.save(any(Item.class)))
                .thenReturn(testItem);
        Mockito
                .when(itemRequestStorage.findById(1L))
                .thenReturn(Optional.of(new ItemRequest()));

        itemService.addItem(1L, new ItemDto(1L,
                "test",
                "description",
                true,
                1L,
                null,
                null,
                null));

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemRequestStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .save(any(Item.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void getItemsTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemStorage.findAllByOwnerIsOrderByIdAsc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testItem)));
        Mockito
                .when(bookingStorage.findFirstByItemIsAndEndBeforeOrderByEndDesc(any(Item.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        Mockito
                .when(bookingStorage.findFirstByItemIsAndStartAfterOrderByStartAsc(any(Item.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        Mockito
                .when(commentStorage.findByItemIsOrderByCreatedDesc(any(Item.class)))
                .thenReturn(List.of());

        itemService.getItems(1L, 0, 20);

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findAllByOwnerIsOrderByIdAsc(any(User.class), any(Pageable.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findFirstByItemIsAndEndBeforeOrderByEndDesc(any(Item.class), any(LocalDateTime.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findFirstByItemIsAndStartAfterOrderByStartAsc(any(Item.class), any(LocalDateTime.class));
        Mockito
                .verify(commentStorage, Mockito.times(1))
                .findByItemIsOrderByCreatedDesc(any(Item.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void patchItemTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemStorage.save(any(Item.class)))
                .then(org.mockito.AdditionalAnswers.returnsFirstArg());
        Mockito
                .when(commentStorage.findByItemIsOrderByCreatedDesc(any(Item.class)))
                .thenReturn(List.of());

        Assertions.assertThrows(UnauthorizedAccessException.class, () -> itemService.patchItem(
                2L,
                1L,
                new ItemDto(null,
                        "updated name",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null)));

        ItemDto answer = itemService.patchItem(1L, 1L, new ItemDto(null,
                "updated name",
                null,
                null,
                null,
                null,
                null,
                null));
        Assertions.assertEquals(answer.getName(), "updated name");
        Assertions.assertEquals(answer.getDescription(), testItem.getDescription());
        Assertions.assertEquals(answer.getAvailable(), testItem.isAvailable());

        answer = itemService.patchItem(1L, 1L, new ItemDto(null,
                null,
                "updated description",
                null,
                null,
                null,
                null,
                null));
        Assertions.assertEquals(answer.getName(), testItem.getName());
        Assertions.assertEquals(answer.getDescription(), "updated description");
        Assertions.assertEquals(answer.getAvailable(), testItem.isAvailable());

        answer = itemService.patchItem(1L, 1L, new ItemDto(null,
                null,
                null,
                false,
                null,
                null,
                null,
                null));
        Assertions.assertEquals(answer.getName(), testItem.getName());
        Assertions.assertEquals(answer.getDescription(), testItem.getDescription());
        Assertions.assertEquals(answer.getAvailable(), false);

        answer = itemService.patchItem(1L, 1L, new ItemDto(null,
                "updated name",
                "updated description",
                false,
                null,
                null,
                null,
                null));
        Assertions.assertEquals(answer.getName(), "updated name");
        Assertions.assertEquals(answer.getDescription(), "updated description");
        Assertions.assertEquals(answer.getAvailable(), false);

        Mockito
                .verify(itemStorage, Mockito.times(5))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(4))
                .save(any(Item.class));
        Mockito
                .verify(commentStorage, Mockito.times(4))
                .findByItemIsOrderByCreatedDesc(any(Item.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void getItemOtherUserTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(bookingStorage.findFirstByItemIsAndEndBeforeOrderByEndDesc(any(Item.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking(
                        testItem,
                        LocalDateTime.of(2000, 1, 21, 12, 30),
                        LocalDateTime.of(2022, 1, 21, 12, 30),
                        testUser,
                        BookingStatus.APPROVED)));
        Mockito
                .when(bookingStorage.findFirstByItemIsAndStartAfterOrderByStartAsc(any(Item.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking(
                        testItem,
                        LocalDateTime.of(2023, 3, 21, 12, 30),
                        LocalDateTime.of(2023, 5, 1, 12, 30),
                        testUser,
                        BookingStatus.APPROVED)));
        Mockito
                .when(commentStorage.findByItemIsOrderByCreatedDesc(any(Item.class)))
                .thenReturn(List.of());

        ItemDto answer = itemService.getItem(2L, 1L);
        Assertions.assertNull(answer.getNextBooking());
        Assertions.assertNull(answer.getLastBooking());
        Assertions.assertNotNull(answer.getComments());

        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(commentStorage, Mockito.times(1))
                .findByItemIsOrderByCreatedDesc(any(Item.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void getItemForOwnerTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(itemStorage.findById(1L))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(bookingStorage.findFirstByItemIsAndEndBeforeOrderByEndDesc(any(Item.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking(
                        testItem,
                        LocalDateTime.of(2000, 1, 21, 12, 30),
                        LocalDateTime.of(2022, 1, 21, 12, 30),
                        testUser,
                        BookingStatus.APPROVED)));
        Mockito
                .when(bookingStorage.findFirstByItemIsAndStartAfterOrderByStartAsc(any(Item.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking(
                        testItem,
                        LocalDateTime.of(2023, 3, 21, 12, 30),
                        LocalDateTime.of(2023, 5, 1, 12, 30),
                        testUser,
                        BookingStatus.APPROVED)));
        Mockito
                .when(commentStorage.findByItemIsOrderByCreatedDesc(any(Item.class)))
                .thenReturn(List.of());

        ItemDto answer = itemService.getItem(1L, 1L);
        Assertions.assertNotNull(answer.getNextBooking());
        Assertions.assertNotNull(answer.getLastBooking());
        Assertions.assertNotNull(answer.getComments());

        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findFirstByItemIsAndEndBeforeOrderByEndDesc(any(Item.class), any(LocalDateTime.class));
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findFirstByItemIsAndStartAfterOrderByStartAsc(any(Item.class), any(LocalDateTime.class));
        Mockito
                .verify(commentStorage, Mockito.times(1))
                .findByItemIsOrderByCreatedDesc(any(Item.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void searchItemsBlankRequestTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(itemStorage.searchItems(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testItem)));

        Collection<ItemDto> dtos = itemService.searchItems("   ", 0, 20);

        Assertions.assertEquals(dtos.size(), 0);

        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void searchItemsNotBlankRequestTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(itemStorage.searchItems(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testItem)));

        Collection<ItemDto> dtos = itemService.searchItems("23", 0, 20);

        Assertions.assertEquals(dtos.size(), 1);

        Mockito
                .verify(itemStorage, Mockito.times(1))
                .searchItems(anyString(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void addCommentNotBookedTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(bookingStorage.findAllByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class),
                        any(Item.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> itemService.addComment(1L, new CommentPostDto("comment"), 1L));

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class),
                        any(Item.class),
                        any(LocalDateTime.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void addCommentTest() {
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        CommentStorage commentStorage = Mockito.mock(CommentStorage.class);
        BookingStorage bookingStorage = Mockito.mock(BookingStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemService itemService = new ItemServiceImpl(itemStorage,
                userStorage,
                commentStorage,
                bookingStorage,
                itemRequestStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Item testItem = new Item(1L, "test", "description", true, testUser, null);

        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(bookingStorage.findAllByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class),
                        any(Item.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking(
                        testItem,
                        LocalDateTime.of(2000, 1, 21, 12, 30),
                        LocalDateTime.of(2022, 1, 21, 12, 30),
                        testUser,
                        BookingStatus.APPROVED)));
        Mockito
                .when(commentStorage.save(any(Comment.class)))
                        .thenReturn(new Comment(1L, testItem, testUser, "text", LocalDateTime.now()));

        itemService.addComment(1L, new CommentPostDto("comment"), 1L);

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(bookingStorage, Mockito.times(1))
                .findAllByBookerAndItemAndEndBeforeOrderByStartDesc(any(User.class),
                        any(Item.class),
                        any(LocalDateTime.class));
        Mockito
                .verify(commentStorage, Mockito.times(1))
                .save(any(Comment.class));
        Mockito.verifyNoMoreInteractions(userStorage, itemStorage, commentStorage, bookingStorage, itemRequestStorage);
    }
}
