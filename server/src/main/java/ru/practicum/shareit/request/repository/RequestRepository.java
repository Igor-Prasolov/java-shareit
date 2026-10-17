package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request AS r WHERE r.requestor.id <> ?1 ORDER BY r.created DESC")
    List<Request> findAllRequestOtherUsers(Long userId);

    @Query("SELECT r FROM Request AS r WHERE r.requestor.id = ?1 ORDER BY r.created DESC")
    List<Request> findAllRequestByRequestorId(Long owner);
}
