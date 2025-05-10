package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BreathingExerciseService {
    private static final Logger logger = LoggerFactory.getLogger(BreathingExerciseService.class);

    private final BreathingExerciseRepository breathingExerciseRepository;

    public BreathingExerciseService(BreathingExerciseRepository breathingExerciseRepository) {
        this.breathingExerciseRepository = breathingExerciseRepository;
    }

    public List<BreathingExercise> getAllExercises() {
        try {
            logger.debug("Fetching all breathing exercises");
            List<BreathingExercise> exercises = breathingExerciseRepository.findAll();
            logger.debug("Found {} exercises", exercises.size());
            return exercises;
        } catch (Exception e) {
            logger.error("Error fetching all exercises", e);
            throw new RuntimeException("Failed to fetch exercises", e);
        }
    }

    public BreathingExercise getExerciseById(Long id) {
        try {
            logger.debug("Fetching exercise with id: {}", id);
            return breathingExerciseRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Exercise not found with id: {}", id);
                        return new EntityNotFoundException("Exercise not found with id: " + id);
                    });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching exercise with id: {}", id, e);
            throw new RuntimeException("Failed to fetch exercise", e);
        }
    }

    public BreathingExercise createExercise(BreathingExercise exercise) {
        try {
            logger.debug("Creating new exercise with name: {}", exercise.getNom());
            BreathingExercise savedExercise = breathingExerciseRepository.save(exercise);
            logger.debug("Successfully created exercise with id: {}", savedExercise.getIdExercice());
            return savedExercise;
        } catch (Exception e) {
            logger.error("Error creating exercise", e);
            throw new RuntimeException("Failed to create exercise", e);
        }
    }

    public BreathingExercise updateExercise(Long id, BreathingExercise exerciseDetails) {
        try {
            logger.debug("Updating exercise with id: {}", id);
            BreathingExercise exercise = getExerciseById(id);
            exercise.setNom(exerciseDetails.getNom());
            exercise.setDescription(exerciseDetails.getDescription());
            exercise.setType(exerciseDetails.getType());
            exercise.setDureeInspiration(exerciseDetails.getDureeInspiration());
            exercise.setDureeExpiration(exerciseDetails.getDureeExpiration());
            exercise.setDureeApnee(exerciseDetails.getDureeApnee());
            exercise.setNombreCycles(exerciseDetails.getNombreCycles());
            BreathingExercise updatedExercise = breathingExerciseRepository.save(exercise);
            logger.debug("Successfully updated exercise with id: {}", id);
            return updatedExercise;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating exercise with id: {}", id, e);
            throw new RuntimeException("Failed to update exercise", e);
        }
    }

    public void deleteExercise(Long id) {
        try {
            logger.debug("Deleting exercise with id: {}", id);
            BreathingExercise exercise = getExerciseById(id);
            breathingExerciseRepository.delete(exercise);
            logger.debug("Successfully deleted exercise with id: {}", id);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting exercise with id: {}", id, e);
            throw new RuntimeException("Failed to delete exercise", e);
        }
    }
} 