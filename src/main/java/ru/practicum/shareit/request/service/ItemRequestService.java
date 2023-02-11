package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestPostDto dto);

    Collection<ItemRequestDto> getRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);
}
