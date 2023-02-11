package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.UserMapper;

public class BookingMapper {
    public static Booking toBooking(BookingPostDto bookingDto, Item item, User booker) {
        return new Booking(item,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                booker,
                BookingStatus.WAITING);
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingItemDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }
}

