package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Запрошен список пользователей");
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto user) {
        log.info("Создание пользователя");
        return userClient.createUser(user);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Запрошен пользователь с id {}", userId);
        return userClient.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с id {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        return userClient.deleteUser(userId);
    }
}
