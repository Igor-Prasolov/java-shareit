package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
}
