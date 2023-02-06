package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class UserServiceTest {
    @Test
    void createUserTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserService userService = new UserServiceImpl(userStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Mockito
                .when(userStorage.save(any(User.class)))
                .thenReturn(testUser);

        userService.createUser(new UserDto());

        Mockito
                .verify(userStorage, Mockito.times(1))
                .save(any(User.class));
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getUserTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserService userService = new UserServiceImpl(userStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Mockito
                .when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));

        userService.getUser(1L);

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUserTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserService userService = new UserServiceImpl(userStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Mockito
                .when(userStorage.findById(1L))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(userStorage.save(any(User.class)))
                .then(org.mockito.AdditionalAnswers.returnsFirstArg());

        UserDto answer = userService.updateUser(1L, new UserDto(null, "updatedName", null));

        Assertions.assertEquals(answer.getName(), "updatedName");
        Assertions.assertEquals(answer.getEmail(), testUser.getEmail());

        answer = userService.updateUser(1L, new UserDto(null, null, "updated@mail.com"));
        Assertions.assertEquals(answer.getName(), testUser.getName());
        Assertions.assertEquals(answer.getEmail(), "updated@mail.com");

        answer = userService.updateUser(1L, new UserDto(null, "updatedName", "updated@mail.com"));
        Assertions.assertEquals(answer.getName(), "updatedName");
        Assertions.assertEquals(answer.getEmail(), "updated@mail.com");

        Mockito
                .verify(userStorage, Mockito.times(3))
                .findById(anyLong());
        Mockito
                .verify(userStorage, Mockito.times(3))
                .save(any(User.class));
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void deleteUserTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserService userService = new UserServiceImpl(userStorage);

        userService.deleteUser(1L);

        Mockito
                .verify(userStorage, Mockito.times(1))
                .deleteById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getUsersTest() {
        UserStorage userStorage = Mockito.mock(UserStorage.class);
        UserService userService = new UserServiceImpl(userStorage);
        User testUser = new User(1L, "testname", "test@mail.com");
        Mockito
                .when(userStorage.findAll())
                .thenReturn(List.of(testUser));

        userService.getUsers();

        Mockito
                .verify(userStorage, Mockito.times(1))
                .findAll();
        Mockito.verifyNoMoreInteractions(userStorage);
    }
}
