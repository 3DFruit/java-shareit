package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.exceptions.UnknownStateException;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @Validated({Create.class}) @RequestBody BookingPostDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.patchBookingStatus(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingsOfUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnknownStateException(state));
        return bookingService.getBookingsOfUser(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsOfUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state",
                                                                 defaultValue = "ALL") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnknownStateException(state));

        return bookingService.getBookingsOfUserItems(userId, bookingState, from, size);
    }
}
