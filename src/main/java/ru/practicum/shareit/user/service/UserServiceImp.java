package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.ValidationException;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        if (user.getEmail() != null) {
            validEmail(user.getEmail());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Этот email уже существует");
        }
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User existingUser = getUserOrThrow(id);
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ConflictException("Этот email уже существует");
            }

        }
        if (user.getName() != null && !user.getName().isEmpty()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            validEmail(user.getEmail());
            existingUser.setEmail(user.getEmail());
        }
        return userMapper.toUserDto(userRepository.update(id, existingUser));
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = getUserOrThrow(id);

        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        getUserOrThrow(id);
        userRepository.deleteUser(id);

    }

    private void validEmail(String email) {
        if (email.isEmpty() || !email.contains("@")) {
            throw new ValidationException("Неправильный формат email");
        }
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} в методе findUserById не найден", userId);
                    return new NotFoundException("Пользователь не найден");
                });
    }

}
