package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.utils.exceptions.UnauthorizedAccessException;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        User owner = userStorage.getUser(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        return ItemMapper.toItemDto(itemStorage.addItem(owner, item, null));
    }

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto patchItem(Long userId, Long itemId, Map<String, Object> updatedData) {
        Item item = itemStorage.getItem(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден предмет с id " + itemId)
        );
        if (!item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Id пользователя(" + userId + ") и владельца не совпадают");
        }
        for (String paramName : updatedData.keySet()) {
            switch (paramName) {
                case "name":
                    item.setName(updatedData.get(paramName).toString());
                    break;
                case "description":
                    item.setDescription(updatedData.get(paramName).toString());
                    break;
                case "available":
                    item.setAvailable((boolean) updatedData.get(paramName));
                    break;
                default:
                    log.warn("Передан не обрабатываемый параметр для обновления Item: {}", paramName);
            }
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден предмет с id " + itemId)
        ));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
