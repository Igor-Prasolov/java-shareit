package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
//    User save(User user);
//
//    User update(Long id, User newUser);
//
//    Optional<User> findUserById(Long id);
//
//    void deleteUser(Long id);
//

    Boolean existsByEmail(String email);

}
