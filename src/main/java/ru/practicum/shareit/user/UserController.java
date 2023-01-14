package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser (@Valid @RequestBody UserDto user) {
        log.info("Создание пользователя");
        return userService.createUser(user);
    }

    @GetMapping(path = "/{userId}")
    public UserDto getUser(@Positive @PathVariable Long userId) {
        log.info("Запрошен пользователь с id {}", userId);
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Positive @PathVariable Long userId, @Valid @RequestBody UserDto user) {
        log.info("Обновление пользователя с id {}", userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
    }
}
