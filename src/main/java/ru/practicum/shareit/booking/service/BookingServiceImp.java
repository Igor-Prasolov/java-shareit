package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingResponseDto createBooking(BookingDto booking, Long userId) {
        User booker = getUserOrThrow(userId);
        if (!booking.getEnd().isAfter(booking.getStart())) {
            log.warn("Некорректные сроки бронирования");
            throw new ValidationException("Некорректные сроки бронирования");
        }
        Item item = getItemOrThrow(booking.getItemId());
        if (!item.getAvailable()) {
            log.warn("Ошибка: вещь уже забронирована");
            throw new ValidationException("Вещь уже забронирована");
        }
        if (userId.equals(item.getOwner().getId())) {
            log.warn("Владелец бронирует свою вещь");
            throw new ConflictException("Вы пытаетесь забронировать свою вещь");
        }

        Booking newBooking = bookingMapper.toBooking(booking);
        newBooking.setItem(item);
        newBooking.setBooker(booker);
        newBooking.setStatus(BookingStatus.WAITING);

        return toBookingResponseDto(bookingRepository.save(newBooking));
    }


    @Override
    public BookingResponseDto updateBooking(Long bookingId, BookingStatus bookingStatus, Long userId) {
        Booking booking = getBookingOrThrow(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Ошибка: бронирует не владелец");
            throw new ForbiddenException("Вы не владелец");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.warn("Конфликт статуса");
            throw new ConflictException("Бронирование уже обработано");
        }
        booking.setStatus(bookingStatus);

        return toBookingResponseDto(bookingRepository.save(booking));
    }


    @Override
    public BookingResponseDto findBookingById(Long id, Long userId) {
        Booking booking = getBookingOrThrow(id);
        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            log.warn("отказ прав доступа");
            throw new NotFoundException("Отказано в доступе");
        }

        return toBookingResponseDto(booking);
    }


    @Override
    public List<BookingResponseDto> findAllBookingByBookerId(Long bookerId, BookingState state) {
        getUserOrThrow(bookerId);

        List<Booking> bookingList = switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(bookerId);
            case PAST -> bookingRepository.findAllByBookerIdPast(bookerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdFuture(bookerId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findAllByBookerIdCurrent(bookerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdStatus(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdStatus(bookerId, BookingStatus.REJECTED);
        };

        return bookingList.stream()
                .map(this::toBookingResponseDto)
                .toList();
    }


    @Override
    public List<BookingResponseDto> findAllBookingByOwnerId(Long ownerId, BookingState state) {
        getUserOrThrow(ownerId);
        List<Booking> bookingList = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerId(ownerId);
            case PAST -> bookingRepository.findAllByItemOwnerIdPast(ownerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdFuture(ownerId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findAllByItemOwnerIdCurrent(ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdStatus(ownerId, BookingStatus.REJECTED);
        };

        return bookingList.stream()
                .map(this::toBookingResponseDto)
                .toList();
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

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Бронь с ID {} не найдена", bookingId);
                    return new NotFoundException("Бронь не найдена");
                });
    }

    private BookingResponseDto toBookingResponseDto(Booking booking) {
        UserDto userDto = userMapper.toUserDto(booking.getBooker());
        ItemDto itemDto = itemMapper.toItemDto(booking.getItem());

        return bookingMapper.toBookingResponseDto(booking, userDto, itemDto);
    }

}
