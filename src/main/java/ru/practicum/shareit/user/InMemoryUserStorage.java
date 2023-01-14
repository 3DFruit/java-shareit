package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

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
        return Optional.ofNullable(userHashMap.get(userId));
    }

    @Override
    public Optional<User> updateUser(Long userId, UserDto user) {
        if (userHashMap.containsKey(userId)) {
            userHashMap.put(userId, new User(userId, user.getName(), user.getEmail()));
        }
        return Optional.ofNullable(userHashMap.get(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userHashMap.remove(userId);
    }
}
