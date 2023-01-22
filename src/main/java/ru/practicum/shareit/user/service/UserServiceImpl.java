package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.utils.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.NotUniqueValueException;
import ru.practicum.shareit.utils.service.ValidationService;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    UserStorage userStorage;
    ValidationService validationService;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, ValidationService validationService) {
        this.userStorage = userStorage;
        this.validationService = validationService;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validationService.validate(userDto);
        User user = userStorage.createUser(userDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> {
                    throw new ObjectNotFoundException("Не найден пользователь с id " + userId);
                });
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> {
                    throw new ObjectNotFoundException("Не найден пользователь с id " + userId);
                });
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userStorage.isUniqueEmail(userDto.getEmail(), userId)) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new NotUniqueValueException("адрес электронной почты");
            }
        }
        return UserMapper.toUserDto(userStorage.updateUser(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userStorage.getUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
