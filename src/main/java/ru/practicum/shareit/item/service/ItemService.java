package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    Collection<ItemDto> getItems(Long userId);

    ItemDto patchItem(Long userId, Long itemId, Map<String, Object> updatedData);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> searchItems(String text);
}
