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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
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

        return bookingMapper.toBookingResponseDto(bookingRepository.save(newBooking));
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

        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto findBookingById(Long id, Long userId) {
        Booking booking = getBookingOrThrow(id);
        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            log.warn("отказ прав доступа");
            throw new NotFoundException("Отказано в доступе");
        }

        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> findAllBookingByBookerId(Long bookerId, BookingState state) {
        getUserOrThrow(bookerId);
        List<Booking> bookingList = bookingRepository.findAllByBookerId(bookerId);
        return filterByState(bookingList, state);
    }

    @Override
    public List<BookingResponseDto> findAllBookingByOwnerId(Long ownerId, BookingState state) {
        getUserOrThrow(ownerId);
        List<Booking> bookingList = bookingRepository.findAllByItemOwnerId(ownerId);
       return filterByState(bookingList, state);
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

    private List<BookingResponseDto> filterByState(List<Booking> bookingList, BookingState state) {
        List<Booking> filterList = new ArrayList<>();

        for (Booking booking : bookingList) {
            boolean bool = switch (state) {
                case ALL -> true;
                case PAST -> booking.getEnd().isBefore(LocalDateTime.now());
                case FUTURE -> booking.getStart().isAfter(LocalDateTime.now());
                case CURRENT -> booking.getStart().isBefore(LocalDateTime.now())
                        && booking.getEnd().isAfter(LocalDateTime.now());
                case WAITING -> booking.getStatus() == BookingStatus.WAITING;
                case REJECTED -> booking.getStatus() == BookingStatus.REJECTED;
            };

            if (bool) {
                filterList.add(booking);
            }
        }

        return bookingMapper.toBookingResponseDtoList(filterList);
    }
}
