package com.clinic.booking.common.exception;

public class BookingDomainException extends RuntimeException {
    public BookingDomainException(String message) {
        super(message);
    }
}
