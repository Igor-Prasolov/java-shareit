package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTestWithContex {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createBookingTest() throws Exception {
        BookingDto request = makeBookingDto(1L,
                LocalDateTime.of(2030, 10, 10, 10, 0),
                LocalDateTime.of(2031, 10, 10, 10, 0),
                5L);

        BookingResponseDto response = makeBookingResponseDto(LocalDateTime.of(2000, 10, 10, 10, 0),
                LocalDateTime.of(2001, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.WAITING);
        response.setId(5L);

        when(bookingService.createBooking(any(BookingDto.class), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 7L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.start", is("2000-10-10T10:00:00")))
                .andExpect(jsonPath("$.end", is("2001-10-10T10:00:00")))
                .andExpect(jsonPath("$.item.id", is(5L), Long.class))
                .andExpect(jsonPath("$.item.name", is("comp")))
                .andExpect(jsonPath("$.booker.id", is(7L), Long.class))
                .andExpect(jsonPath("$.booker.name", is("Igor")));

        verify(bookingService).createBooking(any(BookingDto.class), eq(7L));
    }

    @Test
    void approveBookingTrueTest() throws Exception {
        BookingResponseDto response = makeBookingResponseDto(LocalDateTime.of(2030, 10, 10, 10, 0),
                LocalDateTime.of(2031, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.APPROVED);
        response.setId(3L);

        when(bookingService.updateBooking(anyLong(), any(BookingStatus.class), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.start", is("2030-10-10T10:00:00")))
                .andExpect(jsonPath("$.end", is("2031-10-10T10:00:00")))
                .andExpect(jsonPath("$.item.id", is(5L), Long.class))
                .andExpect(jsonPath("$.item.name", is("comp")))
                .andExpect(jsonPath("$.booker.id", is(7L), Long.class))
                .andExpect(jsonPath("$.booker.name", is("Igor")));

        verify(bookingService).updateBooking(eq(3L), eq(BookingStatus.APPROVED), eq(1L));
    }

    @Test
    void approveBookingFalseTest() throws Exception {
        BookingResponseDto response = makeBookingResponseDto(LocalDateTime.of(2030, 10, 10, 10, 0),
                LocalDateTime.of(2031, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.REJECTED);
        response.setId(3L);

        when(bookingService.updateBooking(anyLong(), any(BookingStatus.class), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.status", is("REJECTED")));

        verify(bookingService).updateBooking(eq(3L), eq(BookingStatus.REJECTED), eq(1L));
    }

    @Test
    void findBookingByIdTest() throws Exception {
        BookingResponseDto response = makeBookingResponseDto(LocalDateTime.of(2030, 10, 10, 10, 0),
                LocalDateTime.of(2031, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.WAITING);
        response.setId(3L);

        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", 3L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.start", is("2030-10-10T10:00:00")))
                .andExpect(jsonPath("$.end", is("2031-10-10T10:00:00")))
                .andExpect(jsonPath("$.item.id", is(5L), Long.class))
                .andExpect(jsonPath("$.item.name", is("comp")))
                .andExpect(jsonPath("$.booker.id", is(7L), Long.class))
                .andExpect(jsonPath("$.booker.name", is("Igor")));

        verify(bookingService).findBookingById(eq(3L), eq(1L));
    }

    @Test
    void findAllBookingByBookerIdDefaultStateTest() throws Exception {
        BookingResponseDto booking = makeBookingResponseDto(LocalDateTime.of(2030, 10, 10, 10, 0),
                LocalDateTime.of(2031, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.WAITING);
        booking.setId(3L);

        when(bookingService.findAllBookingByBookerId(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(3L), Long.class))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].item.name", is("comp")))
                .andExpect(jsonPath("$[0].booker.name", is("Igor")));

        verify(bookingService).findAllBookingByBookerId(eq(1L), eq(BookingState.ALL));
    }

    @Test
    void findAllBookingByBookerIdWithStateTest() throws Exception {
        BookingResponseDto booking = makeBookingResponseDto(LocalDateTime.of(2000, 10, 10, 10, 0),
                LocalDateTime.of(2001, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.APPROVED);
        booking.setId(4L);

        when(bookingService.findAllBookingByBookerId(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].start", is("2000-10-10T10:00:00")));

        verify(bookingService).findAllBookingByBookerId(eq(1L), eq(BookingState.PAST));
    }

    @Test
    void findAllBookingByOwnerIdDefaultStateTest() throws Exception {
        BookingResponseDto booking = makeBookingResponseDto(LocalDateTime.of(2030, 10, 10, 10, 0),
                LocalDateTime.of(2031, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.WAITING);
        booking.setId(3L);

        when(bookingService.findAllBookingByOwnerId(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(3L), Long.class))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].item.name", is("comp")))
                .andExpect(jsonPath("$[0].booker.name", is("Igor")));

        verify(bookingService).findAllBookingByOwnerId(eq(1L), eq(BookingState.ALL));
    }

    @Test
    void findAllBookingByOwnerIdWithStateTest() throws Exception {
        BookingResponseDto booking = makeBookingResponseDto(LocalDateTime.of(2000, 10, 10, 10, 0),
                LocalDateTime.of(2001, 10, 10, 10, 0),
                makeItemDto(5L, "comp"), makeUserDto(7L, "Igor"), BookingStatus.APPROVED);
        booking.setId(4L);

        when(bookingService.findAllBookingByOwnerId(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].start", is("2000-10-10T10:00:00")));

        verify(bookingService).findAllBookingByOwnerId(eq(1L), eq(BookingState.FUTURE));
    }

    // Вспомогательные методы
    private BookingResponseDto makeBookingResponseDto(LocalDateTime start, LocalDateTime end, ItemDto itemDto,
                                                      UserDto bookerDto, BookingStatus status) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemDto);
        bookingResponseDto.setBooker(bookerDto);
        bookingResponseDto.setStatus(status);

        return bookingResponseDto;
    }

    private ItemDto makeItemDto(Long id, String name) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);

        return itemDto;
    }

    private UserDto makeUserDto(Long id, String name) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);

        return userDto;
    }

    private BookingDto makeBookingDto(Long bookingId, LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);

        return bookingDto;
    }
}
