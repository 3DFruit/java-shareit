package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User createUser(UserDto user);

    Optional<User> getUser(Long userId);

    User updateUser(User user);

    void deleteUser(Long userId);

    Collection<User> getUsers();

    boolean isUniqueEmail(String email, Long userId);
}
