package fr.cesi.cesizen.dto;

import java.time.LocalDateTime;

public class BreathingExerciseHistoryDto {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Integer duration; // en secondes
    private LocalDateTime completedAt;

    public BreathingExerciseHistoryDto(Long id, Long exerciseId, String exerciseName, Integer duration, LocalDateTime completedAt) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.duration = duration;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
} 