package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        user.setId(generateIdUser());
        userMap.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(Long id, User newUser) {
        userMap.put(id, newUser);

        return newUser;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public void deleteUser(Long id) {
        userMap.remove(id);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userMap.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private Long generateIdUser() {
        return userMap.keySet().stream()
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
