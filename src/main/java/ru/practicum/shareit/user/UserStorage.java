package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserStorage {
    User createUser (UserDto user);
    Optional<User> getUser(Long userId);
    Optional<User> updateUser(Long userId, UserDto user);
    void deleteUser(Long userId);
}
