package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestAndRequestItemDtoTest {
    private final JacksonTester<RequestDto> jsonReq;
    private final JacksonTester<RequestItemDto> jsonReqAndIt;

    @Test
    void testRequestDto() throws Exception {
        
    }
}
