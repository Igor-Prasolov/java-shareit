package ru.practicum.shareit.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Data
@Slf4j
public class BookingDto {

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @FutureOrPresent
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;

    public void datesValid() {
        if (!getEnd().isAfter(getStart())) {
            log.warn("ошибка в gateway: дата некоректна");
            throw new ValidationException("Даты бронирования некорректны");
        }
    }
}
