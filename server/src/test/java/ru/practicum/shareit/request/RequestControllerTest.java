package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createRequestTest() throws Exception {
        RequestDto request = makeRequestDto();
        RequestDto response = makeRequestDto();
        response.setId(1L);

        when(requestService.createRequest(any(RequestDto.class), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));

        verify(requestService).createRequest(any(RequestDto.class), eq(1L));
    }

    @Test
    void findAllRequestByUserTest() throws Exception {
        RequestDto response = makeRequestDto();
        response.setId(2L);

        when(requestService.findAllRequestByUser(anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(response.getDescription())));

        verify(requestService).findAllRequestByUser(eq(2L));
    }

    @Test
    void findAllRequestOtherUsersTest() throws Exception {
        RequestDto response = makeRequestDto();
        response.setId(3L);

        when(requestService.findAllRequestOtherUsers(anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 3L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(response.getDescription())));

        verify(requestService).findAllRequestOtherUsers(eq(3L));
    }

    @Test
    void findRequestByIdTest() throws Exception {
        RequestDto response = makeRequestDto();
        response.setId(4L);

        when(requestService.findRequestById(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests/{requestId}", 4L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));

        verify(requestService).findRequestById(eq(4L));
    }

    // Вспомогательные методы
    private RequestDto makeRequestDto() {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("zapros");

        return requestDto;
    }
}
