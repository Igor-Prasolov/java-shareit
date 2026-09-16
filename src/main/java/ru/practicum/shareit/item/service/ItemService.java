package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long ownerId);
    Item updateItem(Long itemId, Item item, Long userId);
    Item findItemById(Long id);
    List<Item> findAllItemByOwner(Long ownerId);
    List<Item> searchItems(String text);
    void deleteItemById(Long id);

}
