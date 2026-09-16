package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.repository.UserRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;


    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Этот email уже существует");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findUserById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} в методе findUserById не найден", id);
                    return new NotFoundException("Пользователь не найден");
                });
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ConflictException("Этот email уже существует");
            }

        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        return userRepository.update(id, existingUser);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} в методе findUserById не найден", id);
                    return new NotFoundException("Пользователь не найден");
                });
    }

    @Override
    public void deleteUserById(Long id) {
        findUserById(id);
        userRepository.deleteUser(id);

    }
}




