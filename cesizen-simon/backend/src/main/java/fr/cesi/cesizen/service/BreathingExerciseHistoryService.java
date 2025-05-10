package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.BreathingExerciseHistory;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.BreathingExerciseHistoryRepository;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BreathingExerciseHistoryService {
    private final BreathingExerciseHistoryRepository historyRepository;
    private final BreathingExerciseRepository exerciseRepository;

    public BreathingExerciseHistoryService(BreathingExerciseHistoryRepository historyRepository, BreathingExerciseRepository exerciseRepository) {
        this.historyRepository = historyRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public BreathingExerciseHistory saveHistory(User user, Long exerciseId) {
        BreathingExercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercice non trouvé"));
        BreathingExerciseHistory history = new BreathingExerciseHistory();
        history.setUser(user);
        history.setExercise(exercise);
        history.setCompletedAt(LocalDateTime.now());
        return historyRepository.save(history);
    }

    public List<BreathingExerciseHistory> getHistoryForUser(User user) {
        return historyRepository.findByUser(user);
    }
} 