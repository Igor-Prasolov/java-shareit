package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        UserDto userDto = userMapper.toUserDto(userService.findUserById(userId));

        log.info("Вызов метода findUserById в контроллере");
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User createdUser = userService.createUser(user);

        log.info("Вызов метода createUser из контроллера");
        return userMapper.toUserDto(createdUser);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User updatedUser = userService.updateUser(userId, user);

        log.info("Вызов метода updateUser из контроллера");
        return userMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        log.info("Вызов метода deleteUserById из контроллера");
    }

}
