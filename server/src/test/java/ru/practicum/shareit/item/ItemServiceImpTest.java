package ru.practicum.shareit.item;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final RequestService requestService;
    private final BookingService bookingService;


    // Тесты на создание вещи
    @Test
    void createItemPositiveTest() {
        ItemDto itemDto = makeItemDto();
        UserDto userDto = userService.createUser(makeUserDto("Igor", "igor@mail.ru"));
        ItemDto created = itemService.createItem(itemDto, userDto.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", created.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void createItemWithRequestTest() {
        UserDto owner = userService.createUser(makeUserDto("Masha", "masha@mail.ru"));
        UserDto requestor = userService.createUser(makeUserDto("Igor", "igor@mail.ru"));
        RequestDto requestDto = requestService.createRequest(makeRequestDto(), requestor.getId());
        ItemDto itemDto = makeItemDto();
        itemDto.setRequestId(requestDto.getId());
        ItemDto created = itemService.createItem(itemDto, owner.getId());

        assertThat(created.getId(), notNullValue());
        assertThat(created.getRequestId(), equalTo(requestDto.getId()));
    }

    @Test
    void createItemWithNonRequestIdTest() {
        UserDto owner = userService.createUser(makeUserDto("igor", "igor123@mail.ru"));
        ItemDto itemDto = makeItemDto();
        itemDto.setRequestId(23453425L);

        assertThrows(NotFoundException.class,
                () -> itemService.createItem(itemDto, owner.getId()));
    }

    @Test
    void createItemWithNonOwnerIdTest() {

        assertThrows(NotFoundException.class,
                () -> itemService.createItem(makeItemDto(), 1000000L));
    }

    // Тесты на обновление вещи
    @Test
    void updateItemPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("And", "asf@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), userDto.getId());

        ItemDto updated = new ItemDto();
        updated.setId(itemDto.getId());
        updated.setName("phone");
        updated.setDescription("17");
        updated.setAvailable(false);

        ItemDto result = itemService.updateItem(itemDto.getId(), updated, userDto.getId());

        assertThat(result.getId(), equalTo(updated.getId()));
        assertThat(result.getName(), equalTo(updated.getName()));
        assertThat(result.getDescription(), equalTo(updated.getDescription()));
        assertThat(result.getAvailable(), equalTo(updated.getAvailable()));
    }

    @Test
    void updateItemWithNullFieldsTest() {
        UserDto userDto = userService.createUser(makeUserDto("pr", "asdfasd@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), userDto.getId());

        ItemDto updated = new ItemDto();

        ItemDto result = itemService.updateItem(itemDto.getId(), updated, userDto.getId());

        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void updateItemWithEmptyStringsTest() {
        UserDto userDto = userService.createUser(makeUserDto("asdg", "asdfr@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), userDto.getId());

        ItemDto updated = new ItemDto();
        updated.setDescription("");
        updated.setName("");

        ItemDto result = itemService.updateItem(itemDto.getId(), updated, userDto.getId());

        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void updateItemByNotOwnerTest() {
        UserDto userDto1 = userService.createUser(makeUserDto("tarar", "a1234@mail.ru"));
        UserDto userDto2 = userService.createUser(makeUserDto("adbvs", "a1222234@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), userDto1.getId());
        ItemDto updatedItem = new ItemDto();

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemDto.getId(), updatedItem, userDto2.getId()));
    }

    @Test
    void updateWithNotFoundItemTest() {
        UserDto userDto = userService.createUser(makeUserDto("xzvbcxb", "xcvbmnxfgjfj@mail.ru"));
        ItemDto updatedItem = new ItemDto();

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(312513425632L, updatedItem, userDto.getId()));
    }

    // Тесты на поиск всех вещей владельца
    @Test
    void findAllItemByOwnerPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("lalala", "lalal@mail.ru"));
        ItemDto itemDto1 = itemService.createItem(makeItemDto(), userDto.getId());
        ItemDto itemDto2 = itemService.createItem(
                makeItemDto("car", "white", true), userDto.getId());

        List<ItemDto> result = itemService.findAllItemByOwner(userDto.getId());

        assertThat(result, hasSize(2));

        assertThat(result, hasItem(allOf(
                hasProperty("name", equalTo(itemDto1.getName())),
                hasProperty("description", equalTo(itemDto1.getDescription())),
                hasProperty("available", equalTo(itemDto1.getAvailable())),
                hasProperty("lastBooking", nullValue()),
                hasProperty("nextBooking", nullValue()),
                hasProperty("comments", empty())
        )));

        assertThat(result, hasItem(allOf(
                hasProperty("name", equalTo(itemDto2.getName())),
                hasProperty("description", equalTo(itemDto2.getDescription())),
                hasProperty("available", equalTo(itemDto2.getAvailable())),
                hasProperty("lastBooking", nullValue()),
                hasProperty("nextBooking", nullValue()),
                hasProperty("comments", empty())
        )));
    }

    @Test
    void findAllItemNonOwnerIdTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.findAllItemByOwner(1000000L));
    }

    @Test
    void findAllItemByOwnerWithBookingsAndCommentsTest() {
        UserDto owner = userService.createUser(makeUserDto("adsfasdf", "asdgvx@mail.ru"));
        UserDto booker = userService.createUser(makeUserDto("fehygwe", "vfbsnhdsaa@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), owner.getId());

        LocalDateTime now = LocalDateTime.now();

        BookingResponseDto pastBooking = bookingService.createBooking(makeBookingDto(itemDto.getId(),
                now.minusDays(2), now.minusDays(1)), booker.getId());
         bookingService.updateBooking(pastBooking.getId(), BookingStatus.APPROVED, owner.getId());

        BookingResponseDto futureBooking = bookingService.createBooking(makeBookingDto(itemDto.getId(),
                now.plusDays(1), now.plusDays(2)), booker.getId());
        bookingService.updateBooking(futureBooking.getId(), BookingStatus.APPROVED, owner.getId());

        itemService.createComment(makeCommentDto("asdfza"), itemDto.getId(), booker.getId());

        List<ItemDto> result = itemService.findAllItemByOwner(owner.getId());

        assertThat(result, hasSize(1));

        ItemDto item = result.get(0);

        assertThat(item.getLastBooking().getId(), equalTo(pastBooking.getId()));
        assertThat(item.getLastBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(item.getNextBooking().getId(), equalTo(futureBooking.getId()));
        assertThat(item.getNextBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(item.getComments(), hasSize(1));

        assertThat(item.getComments().get(0).getText(), equalTo("asdfza"));
        assertThat(item.getComments().get(0).getAuthorName(), equalTo("fehygwe"));
    }

    // Тесты на поиск вещи по тексту
    @Test
    void searchItemsPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("asfdgsfd", "dsazbgzh@mail.ru"));
        ItemDto itemDto1 = itemService.createItem(makeItemDto("bb22", "AAAA", true), userDto.getId());
        ItemDto itemDto2 = itemService.createItem(makeItemDto("abcdf", "llll", true), userDto.getId());
        ItemDto itemDto3 = itemService.createItem(makeItemDto("llll", "wasd", true), userDto.getId());

        List<ItemDto> result = itemService.searchItems("LLLL"); // проверка на upperCase

        assertThat(result, hasSize(2));
        assertThat(result, hasItem(allOf(
                hasProperty("name", equalTo(itemDto2.getName())),
                hasProperty("description", equalTo(itemDto2.getDescription())),
                hasProperty("available", equalTo(itemDto2.getAvailable()))
        )));
        assertThat(result, hasItem(allOf(
                hasProperty("name", equalTo(itemDto3.getName())),
                hasProperty("description", equalTo(itemDto3.getDescription())),
                hasProperty("available", equalTo(itemDto3.getAvailable()))
        )));
    }

    @Test
    void searchItemsWithNullTextTest() {
        List<ItemDto> result = itemService.searchItems(null);

        assertThat(result, empty());
    }

    @Test
    void searchItemsWithEmptyTextTest() {
        List<ItemDto> result = itemService.searchItems("");

        assertThat(result, empty());
    }

    //методы удаления вещи
    @Test
    void deleteItemByIdPositiveTest() {
        UserDto owner = userService.createUser(makeUserDto("asdgzcx", "zfdbvcz@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), owner.getId());
        itemService.deleteItemById(itemDto.getId(), owner.getId());

        assertThrows(NotFoundException.class,
                () -> itemService.findItemById(itemDto.getId()));
    }

    @Test
    void deleteNonOwnerItemTest() {
        UserDto owner = userService.createUser(makeUserDto("czxbvz", "zcxv@mail.ru"));
        UserDto otherUser = userService.createUser(makeUserDto("gsnhjfgd", "zx@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), owner.getId());
        assertThrows(NotFoundException.class,
                () -> itemService.deleteItemById(itemDto.getId(), otherUser.getId()));

        ItemDto found = itemService.findItemById(itemDto.getId());

        assertThat(found.getId(), equalTo(itemDto.getId()));
    }

    @Test
    void deleteNotFoundItem() {
        UserDto owner = userService.createUser(makeUserDto("sfdgzx", "cxzvz@mail.ru"));

        assertThrows(NotFoundException.class,
                () -> itemService.deleteItemById(12321543415L, owner.getId()));
    }

    // Тесты поиска вещи по айди
    @Test
    void findItemByIdPositiveTest() {
        UserDto owner = userService.createUser(makeUserDto("vcxbxfgb", "346234@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto("phone", "car", true), owner.getId());

        ItemDto result = itemService.findItemById(itemDto.getId());

        assertThat(result.getId(), equalTo(itemDto.getId()));
        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(result.getComments(), empty());
    }

    @Test
    void findItemByIdNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> itemService.findItemById(12365342L));
    }

    // Тесты создания комментария
    @Test
    void createComment() {
        UserDto owner = userService.createUser(makeUserDto("afdgsfd", "dasgcxz@mail.ru"));
        UserDto booker = userService.createUser(makeUserDto("hjkdftg", "fdg@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), owner.getId());

        LocalDateTime now = LocalDateTime.now();
        BookingResponseDto pastBooking = bookingService.createBooking(makeBookingDto(itemDto.getId(),
                now.minusDays(2), now.minusDays(1)), booker.getId());
        bookingService.updateBooking(pastBooking.getId(), BookingStatus.APPROVED, owner.getId());

        CommentDto commentDto = itemService.createComment(makeCommentDto("fsdg"), itemDto.getId(), booker.getId());

        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo("fsdg"));
        assertThat(commentDto.getAuthorName(), equalTo(booker.getName()));
    }

    @Test
    void createCommentWithNonUserTest() {

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(makeCommentDto("sdfg"), 16L, 16L));
    }

    @Test
    void createCommentWithNonItemTest() {
        UserDto userDto = userService.createUser(makeUserDto("sfdhg", "gfsd@mail.ru"));

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(makeCommentDto("sdfg"), 16L, userDto.getId()));
    }

    @Test
    void createCommentWithoutBookingTest() {
        UserDto owner = userService.createUser(makeUserDto("sgdfg", "fdgb@mail.ru"));
        UserDto otherUser = userService.createUser(makeUserDto("jfdgdf", "fhdgfj@mail.ru"));
        ItemDto itemDto = itemService.createItem(makeItemDto(), owner.getId());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(makeCommentDto("fsdg"), itemDto.getId(), otherUser.getId()));
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

    private BookingDto makeBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        return bookingDto;
    }

    private CommentDto makeCommentDto(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);

        return commentDto;
    }
}
