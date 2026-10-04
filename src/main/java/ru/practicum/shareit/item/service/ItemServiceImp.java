package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;


    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(getUserOrThrow(ownerId));

        return itemMapper.toItemDto(itemRepository.save(item));
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

        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }


    @Override
    public List<ItemDto> findAllItemByOwner(Long ownerId) {
        getUserOrThrow(ownerId);

        List<Comment> commentList = commentRepository.findAllByOwner(ownerId);
        Map<Long, List<Comment>> commentsMap = commentList.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<Item> itemList = itemRepository.findAllItemByOwnerId(ownerId);
        List<ItemDto> itemDtoList = itemMapper.toItemDtoList(itemList);
        for (ItemDto itemDto : itemDtoList) {
            List<Comment> comments = commentsMap.get(itemDto.getId());
            if (comments == null) {
                comments = new ArrayList<>();
            }
            List<CommentDto> commentDtoList = commentMapper.toCommentDtoList(comments);
            itemDto.setComments(commentDtoList);
        }
        return itemDtoList;
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
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto findItemById(Long id) {
        Item item = getItemOrThrow(id);
        ItemDto itemDto = itemMapper.toItemDto(item);
        itemDto.setComments(findByItemId(id));

        return itemDto;
    }

    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {

        User author = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        boolean bool = false;
        for (Booking b : bookings) {
            if (b.getStatus().equals(BookingStatus.APPROVED) && LocalDateTime.now().isAfter(b.getEnd())) {
                bool = true;
            }
        }
        if (!bool) {
            log.warn("Пользователь дает комментарий вещи которую не арендовывал");
            throw new ValidationException("Вы не брали эту вещь в аренду");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<CommentDto> findByItemId(Long itemId) {
        return commentMapper.toCommentDtoList(commentRepository.findByItemId(itemId));
    }

    private List<CommentDto> findAllByOwner(Long ownerId) {
        return commentMapper.toCommentDtoList(commentRepository.findAllByOwner(ownerId));
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вещь с ID {} не найдена", id);
                    return new NotFoundException("Вещь не найдена");
                });
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", userId);
                    return new NotFoundException("Пользователь не найден");
                });
    }

}
