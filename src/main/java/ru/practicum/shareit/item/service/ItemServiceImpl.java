package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.utils.exceptions.UnauthorizedAccessException;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserStorage userStorage;
    CommentStorage commentStorage;
    BookingStorage bookingStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage,
                           UserStorage userStorage,
                           CommentStorage commentStorage,
                           BookingStorage bookingStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.commentStorage = commentStorage;
        this.bookingStorage = bookingStorage;
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        User owner = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        Collection<Item> items = itemStorage.findAllByOwnerIsOrderByIdAsc(owner);
        Collection<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item,
                    BookingMapper.toBookingItemDto(bookingStorage.findLastBookingBeforeDate(item,
                            LocalDateTime.now()).orElse(null)),
                    BookingMapper.toBookingItemDto(bookingStorage.findNextBookingAfterDate(item,
                            LocalDateTime.now()).orElse(null)),
                    commentStorage.findByItemIsOrderByCreatedDesc(item)
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList()))
            );
        }
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto patchItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден предмет с id " + itemId)
        );
        if (!item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Id пользователя(" + userId + ") и владельца не совпадают");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.save(item), commentStorage.findByItemIsOrderByCreatedDesc(item)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден предмет с id " + itemId)
        );
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item,
                    BookingMapper.toBookingItemDto(bookingStorage.findLastBookingBeforeDate(item,
                            LocalDateTime.now()).orElse(null)),
                    BookingMapper.toBookingItemDto(bookingStorage.findNextBookingAfterDate(item,
                            LocalDateTime.now()).orElse(null)),
                    commentStorage.findByItemIsOrderByCreatedDesc(item)
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList()));
        }
        return ItemMapper.toItemDto(item, commentStorage.findByItemIsOrderByCreatedDesc(item)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, CommentPostDto dto, Long itemId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден предмет с id " + itemId)
        );
        if (bookingStorage.findAllByBookerAndItemAndEndBeforeOrderByStartDesc(user,
                item,
                LocalDateTime.now()).size() == 0) {
            throw new UnsupportedOperationException("Нельзя оставить комментарий," +
                    " так как данный предмет не был арендован");
        }
        Comment comment = CommentMapper.toComment(dto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentStorage.save(comment));
    }
}
