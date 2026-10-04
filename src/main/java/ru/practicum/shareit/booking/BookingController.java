package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@PathVariable Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызов метода findBookingById контроллера");
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("Вызов метода findAllBookingByBookerId контроллера");
        return bookingService.findAllBookingByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("вызов метода findAllBookingByOwnerId контроллера");
        return bookingService.findAllBookingByOwnerId(userId, state);
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody @Valid BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызов метода createBooking контроллера");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                    @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        log.info("Вызов метода approveBooking контроллера");
        return bookingService.updateBooking(bookingId, status, userId);
    }

}
