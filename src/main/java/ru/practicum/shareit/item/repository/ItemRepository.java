package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Item update(Long id, Item newItem);

    Optional<Item> findItemById(Long id);

    List<Item> findAllItemByUserId(Long id);

    List<Item> findAll();

    void deleteItem(Long id);



}
