package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> jsonUser;

    @Test
    void testUserDto() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Igor");
        userDto.setEmail("igor@mail.ru");

        JsonContent<UserDto> result = jsonUser.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Igor");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("igor@mail.ru");
    }
}
