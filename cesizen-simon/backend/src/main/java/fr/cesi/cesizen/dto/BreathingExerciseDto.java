package fr.cesi.cesizen.dto;

import fr.cesi.cesizen.model.BreathingExercise;

public class BreathingExerciseDto {
    private Long idExercice;
    private String nom;
    private String description;
    private String type;
    private Integer dureeInspiration;
    private Integer dureeExpiration;
    private Integer dureeApnee;
    private Integer nombreCycles;
    private boolean isOfficial;
    private Long creatorId;
    private Double averageRating;

    public BreathingExerciseDto(BreathingExercise exercise) {
        this.idExercice = exercise.getIdExercice();
        this.nom = exercise.getNom();
        this.description = exercise.getDescription();
        this.type = exercise.getType();
        this.dureeInspiration = exercise.getDureeInspiration();
        this.dureeExpiration = exercise.getDureeExpiration();
        this.dureeApnee = exercise.getDureeApnee();
        this.nombreCycles = exercise.getNombreCycles();
        this.isOfficial = exercise.isOfficial();
        this.creatorId = exercise.getCreator() != null ? exercise.getCreator().getId() : null;
    }

    // Getters and setters
    public Long getIdExercice() { return idExercice; }
    public void setIdExercice(Long idExercice) { this.idExercice = idExercice; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getDureeInspiration() { return dureeInspiration; }
    public void setDureeInspiration(Integer dureeInspiration) { this.dureeInspiration = dureeInspiration; }

    public Integer getDureeExpiration() { return dureeExpiration; }
    public void setDureeExpiration(Integer dureeExpiration) { this.dureeExpiration = dureeExpiration; }

    public Integer getDureeApnee() { return dureeApnee; }
    public void setDureeApnee(Integer dureeApnee) { this.dureeApnee = dureeApnee; }

    public Integer getNombreCycles() { return nombreCycles; }
    public void setNombreCycles(Integer nombreCycles) { this.nombreCycles = nombreCycles; }

    public boolean isOfficial() { return isOfficial; }
    public void setIsOfficial(boolean isOfficial) { this.isOfficial = isOfficial; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
} 