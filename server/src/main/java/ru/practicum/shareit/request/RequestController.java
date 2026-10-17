package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> findAllRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("вызов метода findAllRequestByUser в контроллере");
        return requestService.findAllRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAllRequestOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("вызов метода findAllRequestOtherUsers в контроллере");
        return requestService.findAllRequestOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto findRequestById(@PathVariable Long requestId) {
        log.info("вызов метода findRequestById в контроллере");
        return requestService.findRequestById(requestId);
    }

    @PostMapping
    public RequestDto createRequest(@RequestBody RequestDto requestDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("вызов метода createRequest в контроллере");
        return requestService.createRequest(requestDto, userId);
    }
}
