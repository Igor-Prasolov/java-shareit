package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userService.getUserOrThrow(ownerId));
        itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemMapper.toItem(itemDto);
        Item existingItem = getItemOrThrow(itemId);
        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("Пользователь с ID {} не является владельцем вещи с ID {}", userId, itemId);
            throw new NotFoundException("Редактировать вещь может только ее владелец");
        }
        if (item.getName() != null && !item.getName().isEmpty()) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.update(itemId, existingItem));
    }


    @Override
    public List<ItemDto> findAllItemByOwner(Long ownerId) {
        userService.findUserById(ownerId);
        return itemMapper.toItemDtoList(itemRepository.findAllItemByUserId(ownerId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemMapper.toItemDtoList(itemRepository.search(text));
    }

    @Override
    public void deleteItemById(Long itemId, Long userId) {
        Item existingItem = getItemOrThrow(itemId);
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Удалить вещь может только ее владелец");
        }
        itemRepository.deleteItem(itemId);
    }

    @Override
    public ItemDto findItemById(Long id) {
        Item item = getItemOrThrow(id);

        return itemMapper.toItemDto(item);
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findItemById(id)
                .orElseThrow(() -> {
                    log.warn("Вещь с ID {} в методе findItemById не найдена", id);
                    return new NotFoundException("Вещь не найдена");
                });
    }


}
