package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    Collection<ItemDto> getItems(Long userId, Integer from, Integer size);

    ItemDto patchItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    Collection<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, CommentPostDto dto, Long itemId);
}
