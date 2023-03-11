package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDatesValidator implements ConstraintValidator<BookingDatesConstraint, BookingPostDto> {

    @Override
    public boolean isValid(BookingPostDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}
