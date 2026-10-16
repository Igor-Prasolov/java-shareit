package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpTest {
    private final EntityManager em;
    private final UserService userService;

    // Тесты на создание юзера
    @Test
    void createUserPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("Igor", "igor@mail.ru"));

        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), Matchers.equalTo(userDto.getName()));
        assertThat(user.getEmail(), Matchers.equalTo(userDto.getEmail()));
    }

    @Test
    void createUserNonEmail() {
        assertThrows(ValidationException.class,
                () -> userService.createUser(makeUserDto("Igor", null)));
    }

    @Test
    void createUserWithConflictEmail() {
        userService.createUser(makeUserDto("Igor", "igor123@mail.ru"));

        assertThrows(ConflictException.class,
                () -> userService.createUser(makeUserDto("lupa", "igor123@mail.ru")));
    }

    @Test
    void createUserWithEmptyEmailTest() {
        assertThrows(ValidationException.class,
                () -> userService.createUser(makeUserDto("Igor", "")));
    }

    @Test
    void createUserWithNonAtTest() {
        assertThrows(ValidationException.class,
                () -> userService.createUser(makeUserDto("Igor", "sdfh")));
    }

    //тесты на обновление юзера
    @Test
    void updateUserPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("a", "a@mail.ru"));
        UserDto updated = new UserDto();
        updated.setId(userDto.getId());
        updated.setName("b");
        updated.setEmail("b@mail.ru");

        UserDto result = userService.updateUser(userDto.getId(), updated);

        assertThat(result.getId(), equalTo(updated.getId()));
        assertThat(result.getName(), equalTo(updated.getName()));
        assertThat(result.getEmail(), equalTo(updated.getEmail()));
    }

    @Test
    void updateUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> userService.updateUser(100L, makeUserDto("aaa", "aa@mail.ru")));
    }

    @Test
    void updateUserWithConflictEmail() {
        UserDto userDto1 = userService.createUser(makeUserDto("asdf", "asd@mail.ru"));
        UserDto userDto2 = userService.createUser(makeUserDto("jhghjg", "asd123@mail.ru"));
        userDto2.setEmail("asd@mail.ru");

        assertThrows(ConflictException.class,
                () -> userService.updateUser(userDto2.getId(), userDto2));
    }

    @Test
    void updateUserOnlyNameTest() {
        UserDto userDto = userService.createUser(makeUserDto("w", "w@"));
        UserDto updated = new UserDto();
        updated.setName("lala");
        updated.setId(userDto.getId());

        UserDto result = userService.updateUser(userDto.getId(), updated);

        assertThat(result.getId(), equalTo(updated.getId()));
        assertThat(result.getName(), equalTo(updated.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUserOnlyEmailTest() {
        UserDto userDto = userService.createUser(makeUserDto("w", "w@"));
        UserDto updated = new UserDto();
        updated.setEmail("www@");
        updated.setId(userDto.getId());

        UserDto result = userService.updateUser(userDto.getId(), updated);

        assertThat(result.getId(), equalTo(updated.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(updated.getEmail()));
    }

    @Test
    void updateUserWithEmptyNameTest() {
        UserDto userDto = userService.createUser(makeUserDto("j", "j@"));
        UserDto updated = new UserDto();
        updated.setName("");
        updated.setId(userDto.getId());

        UserDto result = userService.updateUser(userDto.getId(), updated);

        assertThat(result.getId(), equalTo(updated.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUserWithSameEmailTest() {
        UserDto userDto = userService.createUser(makeUserDto("j", "t@"));
        UserDto updated = new UserDto();
        updated.setEmail("t@");
        updated.setId(userDto.getId());

        UserDto result = userService.updateUser(userDto.getId(), updated);

        assertThat(result.getId(), equalTo(updated.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    // Тесты поиска юзера по айди
    @Test
    void findUserByIdPositiveTest() {
        UserDto userDto = userService.createUser(makeUserDto("a", "a@"));
        UserDto result = userService.findUserById(userDto.getId());

        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findUserByIdNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> userService.findUserById(1000L));
    }

    //Тесты удаления юзера по айди
    @Test
    void deleteUserByIdPositive() {
        UserDto userDto = userService.createUser(makeUserDto("a", "asdaz@"));
        userService.deleteUserById(userDto.getId());

        assertThrows(NotFoundException.class,
                () -> userService.findUserById(userDto.getId()));
    }

    @Test
    void deleteUserByIdNotFound() {

        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(123456L));
    }

    // Вспомогательные методы
    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }
}
