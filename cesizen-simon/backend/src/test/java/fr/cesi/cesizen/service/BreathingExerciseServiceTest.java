package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreathingExerciseServiceTest {

    @Mock
    private BreathingExerciseRepository breathingExerciseRepository;

    @InjectMocks
    private BreathingExerciseService breathingExerciseService;

    private BreathingExercise testExercise;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testExercise = new BreathingExercise();
        testExercise.setIdExercice(1L);
        testExercise.setNom("Test Exercise");
        testExercise.setDescription("Test Description");
        testExercise.setType("Test Type");
        testExercise.setDureeInspiration(4);
        testExercise.setDureeExpiration(6);
        testExercise.setDureeApnee(2);
        testExercise.setNombreCycles(5);
        testExercise.setCreator(testUser);
        testExercise.setIsOfficial(false);
    }

    @Test
    void getAllExercises_ShouldReturnAllExercises() {
        List<BreathingExercise> exercises = Arrays.asList(testExercise);
        when(breathingExerciseRepository.findAll()).thenReturn(exercises);

        List<BreathingExercise> result = breathingExerciseService.getAllExercises();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(breathingExerciseRepository).findAll();
    }

    @Test
    void getExerciseById_ShouldReturnExercise_WhenExerciseExists() {
        when(breathingExerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));

        BreathingExercise result = breathingExerciseService.getExerciseById(1L);

        assertNotNull(result);
        assertEquals(testExercise.getIdExercice(), result.getIdExercice());
        assertEquals(testExercise.getNom(), result.getNom());
        verify(breathingExerciseRepository).findById(1L);
    }

    @Test
    void getExerciseById_ShouldThrowException_WhenExerciseDoesNotExist() {
        when(breathingExerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> breathingExerciseService.getExerciseById(1L));
        verify(breathingExerciseRepository).findById(1L);
    }

    @Test
    void createExercise_ShouldCreateAndReturnExercise() {
        when(breathingExerciseRepository.save(any(BreathingExercise.class))).thenReturn(testExercise);

        BreathingExercise result = breathingExerciseService.createExercise(testExercise);

        assertNotNull(result);
        assertEquals(testExercise.getNom(), result.getNom());
        verify(breathingExerciseRepository).save(any(BreathingExercise.class));
    }

    @Test
    void updateExercise_ShouldUpdateAndReturnExercise() {
        BreathingExercise updatedExercise = new BreathingExercise();
        updatedExercise.setNom("Updated Exercise");
        updatedExercise.setDescription("Updated Description");
        updatedExercise.setType("Updated Type");
        updatedExercise.setDureeInspiration(5);
        updatedExercise.setDureeExpiration(7);
        updatedExercise.setDureeApnee(3);
        updatedExercise.setNombreCycles(6);
        updatedExercise.setCreator(testUser);
        updatedExercise.setIsOfficial(true);

        when(breathingExerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(breathingExerciseRepository.save(any(BreathingExercise.class))).thenReturn(updatedExercise);

        BreathingExercise result = breathingExerciseService.updateExercise(1L, updatedExercise);

        assertNotNull(result);
        assertEquals(updatedExercise.getNom(), result.getNom());
        assertEquals(updatedExercise.getDescription(), result.getDescription());
        assertEquals(updatedExercise.getType(), result.getType());
        assertEquals(updatedExercise.getDureeInspiration(), result.getDureeInspiration());
        assertEquals(updatedExercise.getDureeExpiration(), result.getDureeExpiration());
        assertEquals(updatedExercise.getDureeApnee(), result.getDureeApnee());
        assertEquals(updatedExercise.getNombreCycles(), result.getNombreCycles());
        verify(breathingExerciseRepository).findById(1L);
        verify(breathingExerciseRepository).save(any(BreathingExercise.class));
    }

    @Test
    void deleteExercise_ShouldDeleteExercise() {
        when(breathingExerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        doNothing().when(breathingExerciseRepository).delete(testExercise);

        breathingExerciseService.deleteExercise(1L);

        verify(breathingExerciseRepository).findById(1L);
        verify(breathingExerciseRepository).delete(testExercise);
    }
} 