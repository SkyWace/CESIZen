package fr.cesi.cesizen.dto;

import fr.cesi.cesizen.model.ExerciseFeedback;
import java.time.LocalDateTime;

public class ExerciseFeedbackDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long exerciseId;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;

    public ExerciseFeedbackDto(ExerciseFeedback feedback) {
        this.id = feedback.getId();
        this.userId = feedback.getUser().getId();
        this.userName = feedback.getUser().getUsername();
        this.exerciseId = feedback.getExercise().getIdExercice();
        this.comment = feedback.getComment();
        this.rating = feedback.getRating();
        this.createdAt = feedback.getCreatedAt();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 