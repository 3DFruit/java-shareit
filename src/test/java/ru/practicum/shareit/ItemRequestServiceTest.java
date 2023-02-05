package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class ItemRequestServiceTest {
    @Test
    void testCreateRequest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                userStorage,
                itemRequestStorage,
                itemStorage
        );
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "test", "test@test.com")));
        Mockito
                .when(itemRequestStorage.save(any(ItemRequest.class)))
                .thenReturn(new ItemRequest(1L,
                        "описание",
                        new User(1L, "test", "test@test.com"),
                        LocalDateTime.now())
                );
        itemRequestService.createRequest(1L, new ItemRequestPostDto("описание"));

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemRequestStorage, Mockito.times(1))
                .save(any(ItemRequest.class));

        Mockito.verifyNoMoreInteractions(itemRequestStorage, itemStorage, userStorage);
    }

    @Test
    void getRequestsTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                userStorage,
                itemRequestStorage,
                itemStorage
        );
        User testUser = new User(1L, "test", "test@test.com");
        ItemRequest itemRequest = new ItemRequest(1L, "описание", testUser, LocalDateTime.now());
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemRequestStorage.findByRequesterIsOrderByCreatedDesc(any(User.class)))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemStorage.findAllByRequestIn(anyCollection()))
                .thenReturn(new LinkedList<>());

        itemRequestService.getRequests(testUser.getId());

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemRequestStorage, Mockito.times(1))
                .findByRequesterIsOrderByCreatedDesc(any(User.class));
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findAllByRequestIn(anyCollection());

        Mockito.verifyNoMoreInteractions(itemRequestStorage, itemStorage, userStorage);
    }

    @Test
    void getRequestByIdTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                userStorage,
                itemRequestStorage,
                itemStorage
        );
        User testUser = new User(1L, "test", "test@test.com");
        ItemRequest itemRequest = new ItemRequest(1L, "описание", testUser, LocalDateTime.now());
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemRequestStorage.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemStorage.findAllByRequestIn(anyCollection()))
                .thenReturn(new LinkedList<>());

        itemRequestService.getRequestById(testUser.getId(), itemRequest.getId());

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemRequestStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findAllByRequestIn(anyCollection());

        Mockito.verifyNoMoreInteractions(itemRequestStorage, itemStorage, userStorage);
    }

    @Test
    void getAllRequestsTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        ItemRequestStorage itemRequestStorage = Mockito.mock(ItemRequestStorage.class);
        ItemStorage itemStorage = Mockito.mock(ItemStorage.class);
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                userStorage,
                itemRequestStorage,
                itemStorage
        );
        User testUser = new User(1L, "test", "test@test.com");
        ItemRequest itemRequest = new ItemRequest(1L, "описание", testUser, LocalDateTime.now());
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemRequestStorage.findAllByRequesterIsNotOrderByCreatedDesc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        Mockito
                .when(itemStorage.findAllByRequestIn(anyCollection()))
                .thenReturn(new LinkedList<>());

        itemRequestService.getAllRequests(testUser.getId(), 0, 20);

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito
                .verify(itemRequestStorage, Mockito.times(1))
                .findAllByRequesterIsNotOrderByCreatedDesc(any(User.class), any(Pageable.class));
        Mockito
                .verify(itemStorage, Mockito.times(1))
                .findAllByRequestIn(anyCollection());

        Mockito.verifyNoMoreInteractions(itemRequestStorage, itemStorage, userStorage);
    }
}
