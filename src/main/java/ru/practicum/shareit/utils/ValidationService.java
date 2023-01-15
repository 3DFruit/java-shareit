package ru.practicum.shareit.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.utils.exceptions.NotUniqueValueException;

import javax.validation.Valid;

@Service
@Validated
public class ValidationService {
    private final UserStorage userStorage;

    @Autowired
    public ValidationService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validate(UserDto userDto) {
        if (!userStorage.isUniqueEmail(userDto.getEmail(), null)) {
            throw new NotUniqueValueException("адрес электронной почты");
        }
    }

    public void validate(@Valid User user) {
        if (!userStorage.isUniqueEmail(user.getEmail(), user.getId())) {
            throw new NotUniqueValueException("адрес электронной почты");
        }
    }
}
