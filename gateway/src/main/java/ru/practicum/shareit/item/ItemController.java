package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllItemByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Вызов метода findAllItemByOwner в контроллере");
        return itemClient.findAllItemByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId) {

        log.info("Вызов метода findItemById в контроллере");
        return itemClient.findItemById(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        if (text == null || text.isEmpty()) {
            log.info("вызван поиск вещи без текста");
            return ResponseEntity.ok(new ArrayList<>());
        }
        log.info("Вызов метода searchItems в контроллере");
        return itemClient.searchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Вызов метода createItem контроллера");
        return itemClient.createItem(itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызов метода createComment контроллера");
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {


        log.info("Вызов метода updateItem в контроллере");
        return itemClient.updateItem(itemDto, itemId, userId);
    }
}
