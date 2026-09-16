package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllItemByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Вызов метода findAllItemByOwner в контроллере");
        return itemMapper.toItemDtoList(itemService.findAllItemByOwner(ownerId));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId) {
        ItemDto itemDto = itemMapper.toItemDto(itemService.findItemById(itemId));

        log.info("Вызов метода findItemById в контроллере");
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Вызов метода searchItems в контроллере");
        return itemMapper.toItemDtoList(itemService.searchItems(text));
    }

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = itemMapper.toItem(itemDto);
        Item createdItem = itemService.createItem(item, ownerId);

        log.info("Вызов метода createItem контроллера");
        return itemMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item it = itemMapper.toItem(item);
        Item updatedItem = itemService.updateItem(itemId, it, userId);

        log.info("Вызов метода updateItem в контроллере");
        return itemMapper.toItemDto(updatedItem);
    }
}
