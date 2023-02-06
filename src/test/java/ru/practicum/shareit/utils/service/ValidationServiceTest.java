package ru.practicum.shareit.utils.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.utils.exceptions.ValidationException;

import java.time.LocalDateTime;

public class ValidationServiceTest {
    @Test
    void validate() {
        ValidationService validationService = new ValidationService();
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 2, 5, 11, 11);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 2, 6, 11, 11);
        BookingPostDto dto1 = new BookingPostDto(1L, 1L, dateTime2, dateTime1);
        Assertions.assertThrows(ValidationException.class, () -> validationService.validate(dto1));
        BookingPostDto dto2 = new BookingPostDto(1L, 1L, dateTime1, dateTime2);
        Assertions.assertDoesNotThrow(() -> validationService.validate(dto2));
    }
}
