package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImp implements ItemRepository {

    private final Map<Long, Item> itemMap = new HashMap<>();


    @Override
    public Item save(Item item) {
        item.setId(generateIdItem());
        itemMap.put(item.getId(), item);

        return item;
    }

    @Override
    public Item update(Long id, Item newItem) {
        itemMap.put(id, newItem);

        return newItem;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.ofNullable(itemMap.get(id));
    }

    @Override
    public List<Item> findAllItemByUserId(Long id) {
        List<Item> itemsUser = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getOwner().getId().equals(id)) {
                itemsUser.add(item);
            }
        }

        return itemsUser;
    }

    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        for (Item item : itemMap.values()) {
            items.add(item);
        }
        return items;
    }

    @Override
    public void deleteItem(Long id) {
        itemMap.remove(id);
    }

    private Long generateIdItem() {
        return itemMap.keySet().stream()
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
