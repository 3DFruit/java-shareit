package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.utils.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userStorage.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> {
                    throw new ObjectNotFoundException("Не найден пользователь с id " + userId);
                });
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> {
                    throw new ObjectNotFoundException("Не найден пользователь с id " + userId);
                });
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userStorage.deleteById(userId);
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
