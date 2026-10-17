package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImpTest {
    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    //Тесты на создание реквеста
    @Test
    void createRequestPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("I", "i@"));
        RequestDto requestDto = requestService.createRequest(makeRequestDto(), userDto.getId());

        TypedQuery<Request> query = em.createQuery("SELECT r FROM Request AS r WHERE r.id = :id", Request.class);
        Request result = query.setParameter("id", requestDto.getId()).getSingleResult();

        assertThat(result.getId(), equalTo(requestDto.getId()));
        assertThat(result.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(result.getRequestor().getId(), Matchers.equalTo(userDto.getId()));
        assertThat(requestDto.getItems(), empty());
    }

    // Тесты на поиск реквестов юзера
    @Test
    void findAllRequestByUserPositiveTest() {
        UserDto author = userService.createUser(makeUserDto("Is", "is@"));
        UserDto owner = userService.createUser(makeUserDto("Isd", "isd@"));
        RequestDto requestDto = requestService.createRequest(makeRequestDto(), author.getId());
        ItemDto itemToCreated = makeItemDto("car", "black", true);
        itemToCreated.setRequestId(requestDto.getId());
        ItemDto itemDto = itemService.createItem(itemToCreated, owner.getId());
        List<RequestDto> result = requestService.findAllRequestByUser(author.getId());

        assertThat(result, hasSize(1));
        RequestDto req = result.get(0);
        assertThat(req.getId(), equalTo(requestDto.getId()));
        assertThat(req.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(req.getItems(), hasSize(1));

        RequestItemDto foundItem = req.getItems().get(0);
        assertThat(foundItem.getItemId(), equalTo(itemDto.getId()));
        assertThat(foundItem.getName(), equalTo(itemDto.getName()));
        assertThat(foundItem.getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void findAllRequestByUserWithoutItemsTest() {
        UserDto userDto = userService.createUser(makeUserDto("Is", "is@"));
        RequestDto requestDto = requestService.createRequest(makeRequestDto(), userDto.getId());

        List<RequestDto> result = requestService.findAllRequestByUser(userDto.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(requestDto.getId()));
        assertThat(result.get(0).getItems(), empty());
    }

    @Test
    void findAllRequestByUserWithoutRequestsTest() {
        UserDto userDto = userService.createUser(makeUserDto("Is", "is@"));

        List<RequestDto> result = requestService.findAllRequestByUser(userDto.getId());

        assertThat(result, empty());
    }

    @Test
    void findAllRequestByUserNotFoundUserTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.findAllRequestByUser(1000L));
    }

    // Тесты на поиск реквестов других юзеров
    @Test
    void findAllRequestOtherUsersPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("Is", "is@"));
        UserDto viewer = userService.createUser(makeUserDto("Isd", "isd@"));
        RequestDto requestDto = requestService.createRequest(makeRequestDto(), userDto.getId());

        List<RequestDto> result = requestService.findAllRequestOtherUsers(viewer.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(requestDto.getId()));
        assertThat(result.get(0).getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    void findAllRequestOtherUsersWithoutOwnRequestsTest() {
        UserDto userDto = userService.createUser(makeUserDto("Is", "is@"));
        requestService.createRequest(makeRequestDto(), userDto.getId());

        List<RequestDto> result = requestService.findAllRequestOtherUsers(userDto.getId());

        assertThat(result, empty());
    }

    @Test
    void findAllRequestOtherUsersNotFoundUserTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.findAllRequestOtherUsers(1000L));
    }

    // Тесты на поиск реквеста по айди
    @Test
    void findRequestByIdPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("Is", "is@"));
        UserDto owner = userService.createUser(makeUserDto("Isd", "isd@"));
        RequestDto requestDto = requestService.createRequest(makeRequestDto(), userDto.getId());
        ItemDto itemToCreate = makeItemDto("car", "black", true);
        itemToCreate.setRequestId(requestDto.getId());
        ItemDto itemDto = itemService.createItem(itemToCreate, owner.getId());

        RequestDto result = requestService.findRequestById(requestDto.getId());

        assertThat(result.getId(), equalTo(requestDto.getId()));
        assertThat(result.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().get(0).getItemId(), equalTo(itemDto.getId()));
        assertThat(result.getItems().get(0).getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void findRequestByIdNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> requestService.findRequestById(1000L));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        return itemDto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }

    private RequestDto makeRequestDto() {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("zapros");

        return requestDto;
    }
}
