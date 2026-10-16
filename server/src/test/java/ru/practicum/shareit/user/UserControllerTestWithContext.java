package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTestWithContext {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createUserTest() throws Exception {
        UserDto request = makeUserDto();
        UserDto response = makeUserDto("sfdg", "322@");
        response.setId(1L);

        when(userService.createUser(any(UserDto.class)))
                .thenReturn(response);

        mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.email", is(response.getEmail())));

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto request = makeUserDto();
        UserDto response = makeUserDto("us", "us@");
        response.setId(10L);

        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(response);

        mvc.perform(patch("/users/{userId}", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.email", is(response.getEmail())));

        verify(userService).updateUser(eq(10L), any(UserDto.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{userId}", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(eq(100L));
    }

    @Test
    void findUserByIdTest() throws Exception {
        UserDto response = makeUserDto();
        response.setId(99L);

        when(userService.findUserById(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/users/{userId}", 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.email", is(response.getEmail())));

        verify(userService).findUserById(eq(99L));
    }


    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }

    private UserDto makeUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("Igor");
        userDto.setEmail("Igor@mail.ru");

        return userDto;
    }
}
