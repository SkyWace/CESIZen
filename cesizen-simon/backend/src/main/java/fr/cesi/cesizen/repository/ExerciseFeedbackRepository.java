package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.ExerciseFeedback;
import fr.cesi.cesizen.model.BreathingExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ExerciseFeedbackRepository extends JpaRepository<ExerciseFeedback, Long> {
    List<ExerciseFeedback> findByExerciseOrderByCreatedAtDesc(BreathingExercise exercise);

    // Calcul de la moyenne des notes pour un exercice
    @Query("SELECT AVG(f.rating) FROM ExerciseFeedback f WHERE f.exercise = :exercise")
    Double findAverageRatingByExercise(@Param("exercise") BreathingExercise exercise);

    // Trouver un feedback par utilisateur et exercice
    ExerciseFeedback findByUserAndExercise(fr.cesi.cesizen.model.User user, fr.cesi.cesizen.model.BreathingExercise exercise);
} 