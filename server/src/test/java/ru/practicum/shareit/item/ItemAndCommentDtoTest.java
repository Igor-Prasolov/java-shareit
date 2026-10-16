package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemAndCommentDtoTest {

    private final JacksonTester<ItemDto> jsonItem;
    private final JacksonTester<CommentDto> jsonComment;

    @Test
    void testItemDto() throws Exception {

        ItemBookingDto lastBooking = new ItemBookingDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(1L);
        lastBooking.setStart(LocalDateTime.of(2026, 10, 15, 15, 0));
        lastBooking.setEnd(LocalDateTime.of(2030, 10, 15, 15, 0));

        ItemBookingDto nextBooking = new ItemBookingDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(2L);
        nextBooking.setStart(LocalDateTime.of(2040, 10, 15, 15, 0));
        nextBooking.setEnd(LocalDateTime.of(2050, 10, 15, 15, 0));


        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("comp");
        itemDto.setDescription("norm");
        itemDto.setAvailable(true);
        itemDto.setComments(null);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setRequestId(1L);

        JsonContent<ItemDto> result = jsonItem.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("comp");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("norm");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2026-10-15T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2030-10-15T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2040-10-15T15:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2050-10-15T15:00:00");

    }

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("norm");
        commentDto.setAuthorName("Igor");
        commentDto.setCreated(LocalDateTime.of(2026, 10, 15, 15, 0));

        JsonContent<CommentDto> result = jsonComment.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("norm");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Igor");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-10-15T15:00:00");


    }

}
