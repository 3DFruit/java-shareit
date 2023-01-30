package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.utils.exceptions.UnavailableItemException;
import ru.practicum.shareit.utils.exceptions.UnknownStateException;
import ru.practicum.shareit.utils.service.ValidationService;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    BookingStorage bookingStorage;
    UserStorage userStorage;
    ItemStorage itemStorage;
    ValidationService validationService;

    @Autowired
    public BookingServiceImpl(BookingStorage bookingStorage,
                              UserStorage userStorage,
                              ItemStorage itemStorage,
                              ValidationService validationService) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.validationService = validationService;
    }

    @Override
    public BookingDto createBooking(Long userId, BookingPostDto bookingDto) {
        validationService.validate(bookingDto);
        Item item = itemStorage.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ObjectNotFoundException("Не найден предмет с id " + bookingDto.getItemId())
        );
        User booker = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new ObjectNotFoundException("Id пользователя и владельца вещи совпадают");
        }
        if (!item.isAvailable()) {
            throw new UnavailableItemException("Предмет с id " + item.getId() + " не доступен для аренды");
        }
        return BookingMapper.toBookingDto(bookingStorage.save(BookingMapper.toBooking(bookingDto, item, booker)));
    }

    @Override
    public BookingDto patchBookingStatus(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException("Не найдена аренда с id " + bookingId)
        );
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Id пользователя(" + userId + ") и владельца не совпадают");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new UnsupportedOperationException("Попытка изменить статус отличный от WAITING");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingStorage.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException("Не найдена аренда с id " + bookingId)
        );
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ObjectNotFoundException("Id пользователя(" + userId
                    + ") не совпадает с id владельца или арендатора");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookingsOfUser(Long userId, String state) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        }
        catch (IllegalArgumentException e) {
            throw new UnknownStateException(state);
        }
        Collection<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingStorage.findAllByBookerOrderByStartDesc(user);
                break;
            case CURRENT:
                bookings = bookingStorage.findBookingByBookerAndDate(user, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerAndStatusOrderByStartDesc(user,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerAndStatusOrderByStartDesc(user,
                        BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingsOfUserItems(Long userId, String state) {
        User owner = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        }
        catch (IllegalArgumentException e) {
            throw new UnknownStateException(state);
        }
        Collection<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwnerIsOrderByStartDesc(owner);
                break;
            case CURRENT:
                bookings = bookingStorage.findBookingByOwnerAndDate(owner, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(owner, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemOwnerIsAndStartAfterOrderByStartDesc(owner, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemOwnerIsAndStatusOrderByStartDesc(owner,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemOwnerIsAndStatusOrderByStartDesc(owner,
                        BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
