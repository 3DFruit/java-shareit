package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

import java.util.ArrayList;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @Validated({Create.class}) @RequestBody ItemDto item) {
        return itemClient.addItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @Validated({Create.class}) @RequestBody CommentPostDto dto,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(userId, dto, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        return itemClient.getItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                              @RequestParam String text,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        if (text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return itemClient.searchItems(userId, text, from, size);
    }
}
