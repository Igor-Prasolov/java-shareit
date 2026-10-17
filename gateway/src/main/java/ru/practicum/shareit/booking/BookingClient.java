package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;


@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findBookingById(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllBookingByBookerId(Long bookerId, BookingState bookingState) {
        Map<String, Object> param = Map.of(
                "state", bookingState.name()
        );
        return get("?state={state}", bookerId, param);
    }

    public ResponseEntity<Object> findAllBookingByOwnerId(Long ownerId, BookingState bookingState) {
        Map<String, Object> param = Map.of(
                "state", bookingState.name()
        );
        return get("/owner?state={state}", ownerId, param);
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> updateBooking(Long bookingId, Boolean approved, Long userId) {
        Map<String, Object> param = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, param, null);
    }
}