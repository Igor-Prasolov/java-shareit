package ru.practicum.shareit.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @FutureOrPresent
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;

}
