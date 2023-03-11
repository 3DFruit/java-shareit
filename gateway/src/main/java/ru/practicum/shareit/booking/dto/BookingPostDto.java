package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@BookingDatesConstraint(groups = {Create.class})
public class BookingPostDto {
    private Long id;
    @Positive(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private Long itemId;
    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime start;
    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
}
