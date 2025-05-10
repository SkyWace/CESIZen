package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreathingExerciseRepository extends JpaRepository<BreathingExercise, Long> {
    List<BreathingExercise> findByIsOfficialTrue();
    List<BreathingExercise> findByCreator(User creator);
} 