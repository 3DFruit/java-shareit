package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingPostDto bookingDto, Item item, User booker) {
        return new Booking(item,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                booker,
                BookingStatus.WAITING);
    }
    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(),
                item,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                booker,
                bookingDto.getStatus());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getStatus());
    }
}

