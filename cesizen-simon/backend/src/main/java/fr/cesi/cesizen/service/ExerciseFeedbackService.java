package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.ExerciseFeedback;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.ExerciseFeedbackRepository;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import fr.cesi.cesizen.dto.ExerciseFeedbackDto;

@Service
@Transactional
public class ExerciseFeedbackService {
    private final ExerciseFeedbackRepository feedbackRepository;
    private final BreathingExerciseRepository exerciseRepository;

    public ExerciseFeedbackService(ExerciseFeedbackRepository feedbackRepository, 
                                 BreathingExerciseRepository exerciseRepository) {
        this.feedbackRepository = feedbackRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public ExerciseFeedbackDto createFeedback(User user, Long exerciseId, String comment, Integer rating) {
        BreathingExercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        // Vérifier si un feedback existe déjà pour ce user/exercice
        if (feedbackRepository.findByUserAndExercise(user, exercise) != null) {
            throw new IllegalStateException("Vous avez déjà publié un avis pour cet exercice.");
        }

        ExerciseFeedback feedback = new ExerciseFeedback();
        feedback.setUser(user);
        feedback.setExercise(exercise);
        feedback.setComment(comment);
        feedback.setRating(rating);
        feedback.setCreatedAt(LocalDateTime.now());

        ExerciseFeedback savedFeedback = feedbackRepository.save(feedback);
        return new ExerciseFeedbackDto(savedFeedback);
    }

    public List<ExerciseFeedbackDto> getFeedbacksForExercise(Long exerciseId) {
        BreathingExercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        return feedbackRepository.findByExerciseOrderByCreatedAtDesc(exercise)
            .stream()
            .map(ExerciseFeedbackDto::new)
            .collect(Collectors.toList());
    }

    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

    public List<ExerciseFeedbackDto> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
            .map(ExerciseFeedbackDto::new)
            .collect(Collectors.toList());
    }
} 