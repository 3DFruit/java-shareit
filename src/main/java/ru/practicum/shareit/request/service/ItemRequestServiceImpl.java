package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.request.utils.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    UserStorage userStorage;
    ItemRequestStorage itemRequestStorage;
    ItemStorage itemStorage;

    @Autowired
    public ItemRequestServiceImpl(UserStorage userStorage,
                                  ItemRequestStorage itemRequestStorage,
                                  ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemRequestStorage = itemRequestStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestPostDto dto) {
        User requester = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        return ItemRequestMapper.toItemRequestDto(
                itemRequestStorage.save(
                        ItemRequestMapper.toItemRequest(dto, requester)
                )
        );
    }

    @Override
    public Collection<ItemRequestDto> getRequests(Long userId) {
        User requester = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        Collection<ItemRequest> itemRequests = itemRequestStorage.findByRequesterIsOrderByCreatedDesc(requester);
        Collection<Item> items = itemStorage.findAllByRequestIn(itemRequests);
        return toItemRequestDtos(itemRequests, items);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден запрос с id " + requestId)
        );
        Collection<Item> items = itemStorage.findAllByRequestIn(List.of(itemRequest));
        return ItemRequestMapper.toItemRequestDto(itemRequest,
                items.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        Pageable pageable = PageRequest.of(from / size, size);
        Page<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterIsNotOrderByCreatedDesc(user, pageable);
        Collection<Item> items = itemStorage.findAllByRequestIn(itemRequests.toList());
        return toItemRequestDtos(itemRequests.toList(), items);
    }

    private Collection<ItemRequestDto> toItemRequestDtos(Collection<ItemRequest> itemRequests,
                                                         Collection<Item> items) {
        Collection<ItemRequestDto> dtos = new LinkedList<>();
        for (ItemRequest itemRequest : itemRequests) {
            Collection<Item> itemsOfRequest = getItemsOfRequest(itemRequest, items);
            dtos.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemsOfRequest
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList())
            ));
        }
        return dtos;
    }

    private Collection<Item> getItemsOfRequest(ItemRequest request, Collection<Item> items) {
        return items.stream()
                .filter((Item item) -> item.getRequest().equals(request))
                .collect(Collectors.toList());
    }
}
