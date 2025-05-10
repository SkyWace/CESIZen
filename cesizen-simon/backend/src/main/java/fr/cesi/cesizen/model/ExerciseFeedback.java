package fr.cesi.cesizen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_feedback")
public class ExerciseFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private BreathingExercise exercise;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private Integer rating; // 1-5 étoiles

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BreathingExercise getExercise() { return exercise; }
    public void setExercise(BreathingExercise exercise) { this.exercise = exercise; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 