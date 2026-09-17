package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {


        log.info("Вызов метода findUserById в контроллере");
        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {

        log.info("Вызов метода createUser из контроллера");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UserDto userDto) {

        log.info("Вызов метода updateUser из контроллера");
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        log.info("Вызов метода deleteUserById из контроллера");
    }

}
