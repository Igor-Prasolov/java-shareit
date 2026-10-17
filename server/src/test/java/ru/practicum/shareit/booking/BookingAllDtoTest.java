package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingAllDtoTest {

    private final JacksonTester<BookingDto> jsonBookingDto;
    private final JacksonTester<BookingResponseDto> jsonBookingResponseDto;
    private final JacksonTester<ItemBookingDto> jsonItemBookingDto;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = makeBookingDto(LocalDateTime.of(2026, 10, 15, 15, 0),
                LocalDateTime.of(2030, 10, 15, 15, 0), 1L);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-10-15T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-10-15T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

    @Test
    void testBookingResponseDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(5L);
        itemDto.setName("comp");
        UserDto booker = new UserDto();
        booker.setId(7L);
        booker.setName("Igor");
        BookingResponseDto bookingResponseDto = makeBookingResponseDto(LocalDateTime.of(2026, 10, 15, 15, 0),
                LocalDateTime.of(2030, 10, 15, 15, 0), itemDto, booker);

        JsonContent<BookingResponseDto> result = jsonBookingResponseDto.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-10-15T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-10-15T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("comp");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Igor");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }

    @Test
    void testItemBookingDto() throws Exception {
        ItemBookingDto itemBookingDto = makeItemBookingDto(LocalDateTime.of(2026, 10, 15, 15, 0),
                LocalDateTime.of(2030, 10, 15, 15, 0));

        JsonContent<ItemBookingDto> result = jsonItemBookingDto.write(itemBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-10-15T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-10-15T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);

        return bookingDto;
    }

    private BookingResponseDto makeBookingResponseDto(LocalDateTime start, LocalDateTime end, ItemDto itemDto, UserDto bookerDto) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemDto);
        bookingResponseDto.setBooker(bookerDto);
        bookingResponseDto.setStatus(BookingStatus.WAITING);

        return bookingResponseDto;
    }

    private ItemBookingDto makeItemBookingDto(LocalDateTime start, LocalDateTime end) {
        ItemBookingDto itemBookingDto = new ItemBookingDto();
        itemBookingDto.setId(1L);
        itemBookingDto.setBookerId(2L);
        itemBookingDto.setStart(start);
        itemBookingDto.setEnd(end);

        return itemBookingDto;
    }
}
