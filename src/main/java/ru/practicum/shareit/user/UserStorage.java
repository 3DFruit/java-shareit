package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User createUser (UserDto user);
    Optional<User> getUser(Long userId);
    User updateUser(User user);
    void deleteUser(Long userId);

    Collection<User> getUsers();
}
