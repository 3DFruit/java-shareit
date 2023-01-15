package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

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
        return Optional.ofNullable(userHashMap.get(userId).clone());
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
}
