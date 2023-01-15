package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("Запрошен список пользователей");
        return userService.getUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Создание пользователя");
        return userService.createUser(user);
    }

    @GetMapping(path = "/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Запрошен пользователь с id {}", userId);
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> updatedData) {
        log.info("Обновление пользователя с id {}", userId);
        return userService.updateUser(userId, updatedData);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
    }
}
