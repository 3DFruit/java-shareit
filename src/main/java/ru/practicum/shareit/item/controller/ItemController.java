package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                           @Validated({Create.class}) @RequestBody ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
