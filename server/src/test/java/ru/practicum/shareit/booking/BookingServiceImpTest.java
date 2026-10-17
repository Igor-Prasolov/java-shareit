package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    //Тесты для createBooking
    @Test
    void createBookingPositiveTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingDto request = makeBookingDto(item.getId(),
                LocalDateTime.of(2090, 1, 1, 10, 0),
                LocalDateTime.of(2091, 1, 1, 10, 0));

        BookingResponseDto result = bookingService.createBooking(request, booker.getId());

        assertThat(result.getId(), notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result.getStart(), equalTo(LocalDateTime.of(2090, 1, 1, 10, 0)));
        assertThat(result.getEnd(), equalTo(LocalDateTime.of(2091, 1, 1, 10, 0)));
        assertThat(result.getItem().getId(), equalTo(item.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getBooker().getName(), equalTo("Booker"));
    }

    @Test
    void createBookingUserNotFoundTest() {
        UserDto owner = createUser("Owner", "owner@");
        ItemDto item = createItem(owner.getId(), true);
        BookingDto request = makeBookingDto(item.getId(),
                LocalDateTime.of(2090, 1, 1, 10, 0),
                LocalDateTime.of(2091, 1, 1, 10, 0));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(request, 9999L));
    }

    @Test
    void createBookingItemNotFoundTest() {
        UserDto booker = createUser("Booker", "booker@");
        BookingDto request = makeBookingDto(9999L,
                LocalDateTime.of(2090, 1, 1, 10, 0),
                LocalDateTime.of(2091, 1, 1, 10, 0));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBookingItemNotAvailableTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), false);
        BookingDto request = makeBookingDto(item.getId(),
                LocalDateTime.of(2090, 1, 1, 10, 0),
                LocalDateTime.of(2091, 1, 1, 10, 0));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(request, booker.getId()));
    }

    @Test
    void createBookingOwnItemTest() {
        UserDto owner = createUser("Owner", "owner@");
        ItemDto item = createItem(owner.getId(), true);
        BookingDto request = makeBookingDto(item.getId(),
                LocalDateTime.of(2090, 1, 1, 10, 0),
                LocalDateTime.of(2091, 1, 1, 10, 0));

        assertThrows(ConflictException.class, () -> bookingService.createBooking(request, owner.getId()));
    }

    //Тесты для updateBooking
    @Test
    void updateBookingApprovedTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());

        BookingResponseDto result = bookingService.updateBooking(created.getId(), BookingStatus.APPROVED,
                owner.getId());

        assertThat(result.getId(), equalTo(created.getId()));
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void updateBookingRejectedTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());

        BookingResponseDto result = bookingService.updateBooking(created.getId(), BookingStatus.REJECTED,
                owner.getId());

        assertThat(result.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void updateBookingNotFoundTest() {
        UserDto owner = createUser("Owner", "owner@");

        assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(9999L, BookingStatus.APPROVED, owner.getId()));
    }

    @Test
    void updateBookingNotOwnerTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());

        assertThrows(ForbiddenException.class,
                () -> bookingService.updateBooking(created.getId(), BookingStatus.APPROVED, booker.getId()));
    }

    @Test
    void updateBookingAlreadyProcessedTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());
        bookingService.updateBooking(created.getId(), BookingStatus.APPROVED, owner.getId());

        assertThrows(ConflictException.class,
                () -> bookingService.updateBooking(created.getId(), BookingStatus.REJECTED, owner.getId()));
    }

    //Тесты для findBookingById
    @Test
    void findBookingByIdByBookerTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());

        BookingResponseDto result = bookingService.findBookingById(created.getId(), booker.getId());

        assertThat(result.getId(), equalTo(created.getId()));
        assertThat(result.getItem().getId(), equalTo(item.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void findBookingByIdByOwnerTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());

        BookingResponseDto result = bookingService.findBookingById(created.getId(), owner.getId());

        assertThat(result.getId(), equalTo(created.getId()));
    }

    @Test
    void findBookingByIdByStrangerTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        UserDto stranger = createUser("Stranger", "stranger@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto created = createFutureBooking(item.getId(), booker.getId());

        assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(created.getId(), stranger.getId()));
    }

    @Test
    void findBookingByIdNotFoundTest() {
        UserDto owner = createUser("Owner", "owner@");

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(9999L, owner.getId()));
    }

    //Тесты для findAllBookingByBookerId
    @Test
    void findAllBookingByBookerIdAllTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        createPastBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByBookerId(booker.getId(), BookingState.ALL);

        assertThat(result, hasSize(2));
    }

    @Test
    void findAllBookingByBookerIdPastTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto past = createPastBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByBookerId(booker.getId(), BookingState.PAST);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(past.getId()));
    }

    @Test
    void findAllBookingByBookerIdFutureTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createPastBooking(item.getId(), booker.getId());
        BookingResponseDto future = createFutureBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByBookerId(booker.getId(),
                BookingState.FUTURE);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(future.getId()));
    }

    @Test
    void findAllBookingByBookerIdCurrentTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto current = createCurrentBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByBookerId(booker.getId(),
                BookingState.CURRENT);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(current.getId()));
    }

    @Test
    void findAllBookingByBookerIdWaitingTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto waiting = createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto rejected = createFutureBooking(item.getId(), booker.getId());
        bookingService.updateBooking(rejected.getId(), BookingStatus.REJECTED, owner.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByBookerId(booker.getId(),
                BookingState.WAITING);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(waiting.getId()));
    }

    @Test
    void findAllBookingByBookerIdRejectedTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto rejected = createFutureBooking(item.getId(), booker.getId());
        bookingService.updateBooking(rejected.getId(), BookingStatus.REJECTED, owner.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByBookerId(booker.getId(),
                BookingState.REJECTED);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(rejected.getId()));
    }

    @Test
    void findAllBookingByBookerIdUserNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingByBookerId(9999L, BookingState.ALL));
    }

    //Тесты для findAllBookingByOwnerId
    @Test
    void findAllBookingByOwnerIdAllTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        createPastBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByOwnerId(owner.getId(), BookingState.ALL);

        assertThat(result, hasSize(2));
    }

    @Test
    void findAllBookingByOwnerIdPastTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto past = createPastBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByOwnerId(owner.getId(), BookingState.PAST);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(past.getId()));
    }

    @Test
    void findAllBookingByOwnerIdFutureTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createPastBooking(item.getId(), booker.getId());
        BookingResponseDto future = createFutureBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByOwnerId(owner.getId(),
                BookingState.FUTURE);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(future.getId()));
    }

    @Test
    void findAllBookingByOwnerIdCurrentTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto current = createCurrentBooking(item.getId(), booker.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByOwnerId(owner.getId(),
                BookingState.CURRENT);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(current.getId()));
    }

    @Test
    void findAllBookingByOwnerIdWaitingTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        BookingResponseDto waiting = createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto rejected = createFutureBooking(item.getId(), booker.getId());
        bookingService.updateBooking(rejected.getId(), BookingStatus.REJECTED, owner.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByOwnerId(owner.getId(),
                BookingState.WAITING);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(waiting.getId()));
    }

    @Test
    void findAllBookingByOwnerIdRejectedTest() {
        UserDto owner = createUser("Owner", "owner@");
        UserDto booker = createUser("Booker", "booker@");
        ItemDto item = createItem(owner.getId(), true);
        createFutureBooking(item.getId(), booker.getId());
        BookingResponseDto rejected = createFutureBooking(item.getId(), booker.getId());
        bookingService.updateBooking(rejected.getId(), BookingStatus.REJECTED, owner.getId());

        List<BookingResponseDto> result = bookingService.findAllBookingByOwnerId(owner.getId(),
                BookingState.REJECTED);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(rejected.getId()));
    }

    @Test
    void findAllBookingByOwnerIdUserNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingByOwnerId(9999L, BookingState.ALL));
    }

    //Вспомогательные методы
    private UserDto createUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userService.createUser(userDto);
    }

    private ItemDto createItem(Long ownerId, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("comp");
        itemDto.setDescription("good comp");
        itemDto.setAvailable(available);

        return itemService.createItem(itemDto, ownerId);
    }

    private BookingDto makeBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        return bookingDto;
    }

    private BookingResponseDto createFutureBooking(Long itemId, Long bookerId) {
        return bookingService.createBooking(makeBookingDto(itemId,
                LocalDateTime.of(2090, 1, 1, 10, 0),
                LocalDateTime.of(2091, 1, 1, 10, 0)), bookerId);
    }

    private BookingResponseDto createPastBooking(Long itemId, Long bookerId) {
        return bookingService.createBooking(makeBookingDto(itemId,
                LocalDateTime.of(2000, 1, 1, 10, 0),
                LocalDateTime.of(2001, 1, 1, 10, 0)), bookerId);
    }

    private BookingResponseDto createCurrentBooking(Long itemId, Long bookerId) {
        return bookingService.createBooking(makeBookingDto(itemId,
                LocalDateTime.of(2000, 1, 1, 10, 0),
                LocalDateTime.of(2080, 1, 1, 10, 0)), bookerId);
    }
}