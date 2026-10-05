package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


@Component
public class BookingMapper {

    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        return booking;
    }

    public BookingResponseDto toBookingResponseDto(Booking booking, UserDto userDto, ItemDto itemDto) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        bookingResponseDto.setBooker(userDto);
        bookingResponseDto.setItem(itemDto);

        return bookingResponseDto;
    }

    public ItemBookingDto toItemBookingDto(Booking booking) {
        ItemBookingDto itemBookingDto = new ItemBookingDto();
        itemBookingDto.setId(booking.getId());
        itemBookingDto.setStart(booking.getStart());
        itemBookingDto.setEnd(booking.getEnd());
        itemBookingDto.setBookerId(booking.getBooker().getId());

        return itemBookingDto;
    }

}
