package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(User user, ItemDto item, ItemRequest itemRequest);

    Collection<Item> getItems(Long userId);

    Item updateItem(Item item);

    Optional<Item> getItem(Long itemId);

    Collection<Item> searchItems(String text);
}