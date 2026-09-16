package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;


    @Override
    public Item createItem(Item item, Long ownerId) {
        item.setOwner(userService.findUserById(ownerId));
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, Item item, Long userId) {
        Item existingItem = itemRepository.findItemById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь с ID {} в методе updateItem не найдена", itemId);
                    return new NotFoundException("Вещь не найдена");
                });
        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("Пользователь с ID {} не является владельцем вещи с ID {}", userId, itemId);
            throw new  NotFoundException("Редактировать вещь может только ее владелец");
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemRepository.update(itemId, existingItem);
    }

    @Override
    public Item findItemById(Long id) {
        return itemRepository.findItemById(id)
                .orElseThrow(() -> {
                    log.warn("Вещь с ID {} в методе findItemById не найдена", id);
                    return new NotFoundException("Вещь не найдена");
                });
    }

    @Override
    public List<Item> findAllItemByOwner(Long ownerId) {
        userService.findUserById(ownerId);
        return itemRepository.findAllItemByUserId(ownerId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> ((item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable() == true)
                )
                .collect(Collectors.toList());

        return items;
    }

    @Override
    public void deleteItemById(Long id) {
        findItemById(id);
        itemRepository.deleteItem(id);
    }
}
