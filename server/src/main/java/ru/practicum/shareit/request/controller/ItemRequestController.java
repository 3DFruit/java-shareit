package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestPostDto dto) {
        return itemRequestService.createRequest(userId, dto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        return itemRequestService.getAllRequests(userId, from, size);
    }
}
