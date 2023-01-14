package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.ObjectNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userStorage.createUser(userDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(()-> {
            throw new ObjectNotFoundException("Не найден польхователь с id " + userId);
        });
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userStorage.updateUser(userId, userDto)
                .orElseThrow(()-> {
                    throw new ObjectNotFoundException("Не найден польхователь с id " + userId);
                });
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
