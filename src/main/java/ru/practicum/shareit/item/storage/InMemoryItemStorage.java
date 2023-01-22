package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Long, Item> itemHashMap = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Item addItem(User user, ItemDto item, ItemRequest itemRequest) {
        Item newItem = new Item(nextId++,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user,
                itemRequest);
        itemHashMap.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Collection<Item> getItems(Long userId) {
        return itemHashMap.values().stream()
                .filter((item) -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Item item) {
        itemHashMap.put(item.getId(), item);
        return itemHashMap.get(item.getId()).clone();
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        return Optional.ofNullable(itemHashMap.get(itemId) != null ? itemHashMap.get(itemId).clone() : null);
    }

    @Override
    public Collection<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new LinkedList<>();
        }
        return itemHashMap.values().stream()
                .filter(Item::isAvailable)
                .filter((item) -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
