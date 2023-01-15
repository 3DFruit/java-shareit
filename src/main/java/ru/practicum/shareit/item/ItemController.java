package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

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
                           @Valid @RequestBody ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody Map<String, Object> updatedData) {
        return itemService.patchItem(userId, itemId, updatedData);
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
