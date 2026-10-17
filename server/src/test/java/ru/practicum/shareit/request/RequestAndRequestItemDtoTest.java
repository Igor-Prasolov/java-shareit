package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestAndRequestItemDtoTest {
    private final JacksonTester<RequestDto> jsonReq;
    private final JacksonTester<RequestItemDto> jsonReqAndIt;

    @Test
    void testRequestDto() throws Exception {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("desc");
        requestDto.setCreated(LocalDateTime.of(2026, 10, 15, 15, 0));
        requestDto.setItems(List.of(makeRequestItemDto()));

        JsonContent<RequestDto> result = jsonReq.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-10-15T15:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("abc");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(10);
    }

    @Test
    void testRequestItemDto() throws Exception {
        RequestItemDto requestItemDto = makeRequestItemDto();

        JsonContent<RequestItemDto> result = jsonReqAndIt.write(requestItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("abc");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(10);

    }

    private RequestItemDto makeRequestItemDto() {
        RequestItemDto requestItemDto = new RequestItemDto();
        requestItemDto.setName("abc");
        requestItemDto.setItemId(1L);
        requestItemDto.setOwnerId(10L);

        return requestItemDto;
    }
}

