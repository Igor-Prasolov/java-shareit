package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {

        log.info("Вызов метода findUserById в контроллере");
        return userClient.findUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {

        log.info("Вызов метода createUser из контроллера");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UserDto userDto) {

        log.info("Вызов метода updateUser из контроллера");
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        log.info("Вызов метода deleteUserById из контроллера");
        return userClient.deleteUserById(userId);
    }

}
