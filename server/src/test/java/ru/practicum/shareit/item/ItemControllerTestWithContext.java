package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
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


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTestWithContext {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;


    @Test
    void createItemTest() throws Exception {
        ItemDto request = makeItemDto();
        ItemDto response = makeItemDto();
        response.setId(1L);

        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.available", is(response.getAvailable())));

        verify(itemService).createItem(any(ItemDto.class), eq(1L));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto request = makeCommentDto("fsdvb");
        CommentDto response = makeCommentDto("fsdvb");
        response.setId(1L);
        response.setAuthorName("igor");

        when(itemService.createComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items/{itemId}/comment", 5L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(response.getText())))
                .andExpect(jsonPath("$.authorName", is(response.getAuthorName())));

        verify(itemService).createComment(any(CommentDto.class), eq(5L), eq(1L));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto request = makeItemDto("bike", "green", true);
        ItemDto response = makeItemDto("mot", "red", true);
        response.setId(5L);

        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/items/{itemId}", 5L)
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.available", is(response.getAvailable())));

        verify(itemService).updateItem(eq(5L), any(ItemDto.class), eq(10L));
    }

    @Test
    void findAllItemByOwnerTest() throws Exception {
        ItemDto response = makeItemDto("car", "red", true);
        response.setId(1L);

        when(itemService.findAllItemByOwner(anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 6L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(response.getName())))
                .andExpect(jsonPath("$[0].description", is(response.getDescription())))
                .andExpect(jsonPath("$[0].available", is(response.getAvailable())));

        verify(itemService).findAllItemByOwner(eq(6L));
    }

    @Test
    void findItemByIdTest() throws Exception {
        ItemDto response = makeItemDto("tesla", "black", true);
        response.setId(20L);

        when(itemService.findItemById(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/items/{itemId}", 20L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.available", is(response.getAvailable())));

        verify(itemService).findItemById(eq(20L));
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDto response = makeItemDto("audi", "black", true);
        response.setId(11L);

        when(itemService.searchItems(anyString()))
                .thenReturn(List.of(response));

        mvc.perform(get("/items/search")
                        .param("text", "audi")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(response.getName())))
                .andExpect(jsonPath("$[0].description", is(response.getDescription())))
                .andExpect(jsonPath("$[0].available", is(response.getAvailable())));

        verify(itemService).searchItems(eq("audi"));
    }

    // Вспомогательные методы
    private ItemDto makeItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("comp");
        itemDto.setDescription("norm");
        itemDto.setAvailable(true);

        return itemDto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        return itemDto;
    }

    private CommentDto makeCommentDto(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);

        return commentDto;
    }

}
