package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    BookingStorage bookingStorage;
    UserStorage userStorage;
    ItemStorage itemStorage;

    @Autowired
    public BookingServiceImpl(BookingStorage bookingStorage,
                              UserStorage userStorage,
                              ItemStorage itemStorage) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingPostDto bookingDto) {
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
    @Transactional
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
    public Collection<BookingDto> getBookingsOfUser(Long userId, BookingState state, Integer from, Integer size) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerOrderByStartDesc(user, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findBookingByBookerAndDate(user, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerAndEndBeforeOrderByStartDesc(user,
                        LocalDateTime.now(),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerAndStartAfterOrderByStartDesc(user,
                        LocalDateTime.now(),
                        pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerAndStatusOrderByStartDesc(user,
                        BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerAndStatusOrderByStartDesc(user,
                        BookingStatus.REJECTED,
                        pageable);
                break;
            default:
                bookings = new PageImpl<>(List.of());
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingsOfUserItems(Long userId, BookingState state, Integer from, Integer size) {
        User owner = userStorage.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Не найден пользователь с id " + userId)
        );
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwnerIsOrderByStartDesc(owner, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findBookingByOwnerAndDate(owner, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemOwnerIsAndEndBeforeOrderByStartDesc(owner,
                        LocalDateTime.now(),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemOwnerIsAndStartAfterOrderByStartDesc(owner,
                        LocalDateTime.now(),
                        pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemOwnerIsAndStatusOrderByStartDesc(owner,
                        BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemOwnerIsAndStatusOrderByStartDesc(owner,
                        BookingStatus.REJECTED,
                        pageable);
                break;
            default:
                bookings = new PageImpl<>(List.of());
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
