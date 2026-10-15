package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@PathVariable Long bookingId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызов метода findBookingById контроллера");
        return bookingClient.findBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("Вызов метода findAllBookingByBookerId контроллера");
        return bookingClient.findAllBookingByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("вызов метода findAllBookingByOwnerId контроллера");
        return bookingClient.findAllBookingByOwnerId(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызов метода createBooking контроллера");
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                                 @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызов метода approveBooking контроллера");
        return bookingClient.updateBooking(bookingId, approved, userId);
    }

}