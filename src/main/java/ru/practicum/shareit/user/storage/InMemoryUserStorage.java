package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> userHashMap = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public User createUser(UserDto user) {
        User newUser = new User(nextId++, user.getName(), user.getEmail());
        userHashMap.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(userHashMap.get(userId) != null ? userHashMap.get(userId).clone() : null);
    }

    @Override
    public User updateUser(User user) {
        userHashMap.put(user.getId(), user);
        return userHashMap.get(user.getId()).clone();
    }

    @Override
    public void deleteUser(Long userId) {
        userHashMap.remove(userId);
    }

    @Override
    public Collection<User> getUsers() {
        return userHashMap.values();
    }

    @Override
    public boolean isUniqueEmail(String email, Long userId) {
        if (userId == null) {
            return userHashMap.values().stream().noneMatch(user -> user.getEmail().equals(email));
        }
        return userHashMap.values().stream()
                .filter(user -> !user.getId().equals(userId))
                .noneMatch(user -> user.getEmail().equals(email));
    }
}