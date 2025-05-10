package fr.cesi.cesizen.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exercice")
public class BreathingExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExercice;

    private String nom;
    private String description;
    private String type;
    private Integer dureeInspiration;
    private Integer dureeExpiration;
    private Integer dureeApnee;
    private Integer nombreCycles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    private boolean isOfficial = false;

    public boolean isOfficial() { return isOfficial; }
    public void setIsOfficial(boolean isOfficial) { this.isOfficial = isOfficial; }
}
