package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    @PostMapping
    public ItemDto addItem(@RequestHeader(name = "X-Sharer-User-Id", defaultValue = "-1") Long userId,
                           @Valid @RequestBody ItemDto item) {
        return null;
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id", defaultValue = "-1") Long userId) {
        return null;
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(name = "X-Sharer-User-Id", defaultValue = "-1") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody Map<String, Object> updatedData) {
        return null;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return null;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return null;
    }
}
