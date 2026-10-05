package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @FutureOrPresent
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;

    private BookingStatus status;
}
