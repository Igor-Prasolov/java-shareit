package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAllItemByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> findItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> param = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, param);
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, Long ownerId) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

}
