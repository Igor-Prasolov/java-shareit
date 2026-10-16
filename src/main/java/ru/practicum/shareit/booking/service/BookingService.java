package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;


import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingDto booking, Long userId);

    BookingResponseDto updateBooking(Long bookingId, BookingStatus bookingStatus, Long userId);

    BookingResponseDto findBookingById(Long id, Long userId);

    List<BookingResponseDto> findAllBookingByBookerId(Long bookerId, BookingState state);

    List<BookingResponseDto> findAllBookingByOwnerId(Long ownerId, BookingState state);

}
