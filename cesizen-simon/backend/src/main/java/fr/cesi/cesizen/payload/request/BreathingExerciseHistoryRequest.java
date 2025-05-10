package fr.cesi.cesizen.payload.request;

import jakarta.validation.constraints.NotNull;

public class BreathingExerciseHistoryRequest {
    @NotNull
    private Long exerciseId;

    public Long getExerciseId() {
        return exerciseId;
    }
    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }
} 