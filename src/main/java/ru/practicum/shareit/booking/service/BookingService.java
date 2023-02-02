package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.utils.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingPostDto bookingDto);

    BookingDto patchBookingStatus(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getBookingsOfUser(Long userId, BookingState state);

    Collection<BookingDto> getBookingsOfUserItems(Long userId, BookingState state);
}
