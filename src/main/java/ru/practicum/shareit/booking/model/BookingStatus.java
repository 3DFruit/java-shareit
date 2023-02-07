package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    @Override
    public String toString() {
        return this.name();
    }
}
