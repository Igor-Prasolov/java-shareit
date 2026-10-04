package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Long ownerId);

    ItemDto updateItem(Long itemId, ItemDto item, Long userId);

    ItemDto findItemById(Long id);

    List<ItemDto> findAllItemByOwner(Long ownerId);

    List<ItemDto> searchItems(String text);

    void deleteItemById(Long itemId, Long userId);

}
