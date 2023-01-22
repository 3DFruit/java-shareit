package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(Long userId);

    UserDto updateUser(Long userId, Map<String, Object> updatedData);

    void deleteUser(Long userId);

    Collection<UserDto> getUsers();
}
