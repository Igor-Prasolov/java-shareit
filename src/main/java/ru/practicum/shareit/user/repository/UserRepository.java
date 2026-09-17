package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(Long id, User newUser);

    Optional<User> findUserById(Long id);

    void deleteUser(Long id);

    Boolean existsByEmail(String email);

}
