package fr.cesi.cesizen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BreathingExerciseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private BreathingExercise exercise;

    private LocalDateTime completedAt;

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BreathingExercise getExercise() { return exercise; }
    public void setExercise(BreathingExercise exercise) { this.exercise = exercise; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
} 