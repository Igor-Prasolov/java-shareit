package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllItemByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Вызов метода findAllItemByOwner в контроллере");
        return itemService.findAllItemByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId) {

        log.info("Вызов метода findItemById в контроллере");
        return itemService.findItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Вызов метода searchItems в контроллере");
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {

        log.info("Вызов метода createItem контроллера");
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {


        log.info("Вызов метода updateItem в контроллере");
        return itemService.updateItem(itemId, item, userId);
    }
}
