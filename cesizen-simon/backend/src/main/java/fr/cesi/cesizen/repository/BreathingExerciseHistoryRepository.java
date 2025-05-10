package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.BreathingExerciseHistory;
import fr.cesi.cesizen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BreathingExerciseHistoryRepository extends JpaRepository<BreathingExerciseHistory, Long> {
    List<BreathingExerciseHistory> findByUser(User user);
} 