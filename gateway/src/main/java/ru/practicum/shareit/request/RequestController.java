package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> findAllRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("вызов метода findAllRequestByUser в контроллере");
        return requestClient.findAllRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("вызов метода findAllRequestOtherUsers в контроллере");
        return requestClient.findAllRequestOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@PathVariable Long requestId) {
        log.info("вызов метода findRequestById в контроллере");
        return requestClient.findRequestById(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody RequestDto requestDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("вызов метода createRequest в контроллере");
        return requestClient.createRequest(requestDto, userId);
    }
}
