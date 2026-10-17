package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    UserDto findUserById(Long id);

    void deleteUserById(Long id);

}
