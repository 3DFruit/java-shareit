package ru.practicum.shareit.utils.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.utils.exceptions.ValidationException;

@Service
public class ValidationService {
    public void validate(BookingPostDto bookingDto) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException("Время начала должно быть раньше времени окончания аренды");
        }
    }
}
