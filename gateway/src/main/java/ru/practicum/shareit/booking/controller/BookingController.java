package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.exceptions.UnknownStateException;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @Validated({Create.class}) @RequestBody BookingPostDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam(name = "approved") Boolean isApproved) {
        return bookingClient.patchBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnknownStateException(state));
        return bookingClient.getBookingsOfUser(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state",
                                                                 defaultValue = "ALL") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0 || size < 1) {
            throw new UnsupportedOperationException("Неверные параметры запроса");
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnknownStateException(state));

        return bookingClient.getBookingsOfUserItems(userId, bookingState, from, size);
    }
}
