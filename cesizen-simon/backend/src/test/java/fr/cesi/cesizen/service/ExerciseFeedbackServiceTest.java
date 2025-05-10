package fr.cesi.cesizen.service;

import fr.cesi.cesizen.dto.ExerciseFeedbackDto;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.ExerciseFeedback;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import fr.cesi.cesizen.repository.ExerciseFeedbackRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseFeedbackServiceTest {

    @Mock
    private ExerciseFeedbackRepository feedbackRepository;

    @Mock
    private BreathingExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseFeedbackService feedbackService;

    private User testUser;
    private BreathingExercise testExercise;
    private ExerciseFeedback testFeedback;
    private ExerciseFeedbackDto testFeedbackDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        testUser.setRoles(roles);

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

        testFeedback = new ExerciseFeedback();
        testFeedback.setId(1L);
        testFeedback.setUser(testUser);
        testFeedback.setExercise(testExercise);
        testFeedback.setComment("Test Comment");
        testFeedback.setRating(5);
        testFeedback.setCreatedAt(LocalDateTime.now());

        testFeedbackDto = new ExerciseFeedbackDto(testFeedback);
    }

    @Test
    void createFeedback_Success() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(feedbackRepository.findByUserAndExercise(testUser, testExercise)).thenReturn(null);
        when(feedbackRepository.save(any(ExerciseFeedback.class))).thenReturn(testFeedback);

        ExerciseFeedbackDto result = feedbackService.createFeedback(testUser, 1L, "Test Comment", 5);

        assertNotNull(result);
        assertEquals(testFeedback.getId(), result.getId());
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals(testExercise.getIdExercice(), result.getExerciseId());
        assertEquals("Test Comment", result.getComment());
        assertEquals(5, result.getRating());
        verify(exerciseRepository).findById(1L);
        verify(feedbackRepository).findByUserAndExercise(testUser, testExercise);
        verify(feedbackRepository).save(any(ExerciseFeedback.class));
    }

    @Test
    void createFeedback_ExerciseNotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> feedbackService.createFeedback(testUser, 1L, "Test Comment", 5));
        verify(exerciseRepository).findById(1L);
        verify(feedbackRepository, never()).findByUserAndExercise(any(), any());
        verify(feedbackRepository, never()).save(any(ExerciseFeedback.class));
    }

    @Test
    void createFeedback_FeedbackAlreadyExists() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(feedbackRepository.findByUserAndExercise(testUser, testExercise)).thenReturn(testFeedback);

        assertThrows(IllegalStateException.class, () -> feedbackService.createFeedback(testUser, 1L, "Test Comment", 5));
        verify(exerciseRepository).findById(1L);
        verify(feedbackRepository).findByUserAndExercise(testUser, testExercise);
        verify(feedbackRepository, never()).save(any(ExerciseFeedback.class));
    }

    @Test
    void getFeedbacksForExercise_Success() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(feedbackRepository.findByExerciseOrderByCreatedAtDesc(testExercise)).thenReturn(Arrays.asList(testFeedback));

        List<ExerciseFeedbackDto> result = feedbackService.getFeedbacksForExercise(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFeedback.getId(), result.get(0).getId());
        assertEquals(testUser.getId(), result.get(0).getUserId());
        assertEquals(testExercise.getIdExercice(), result.get(0).getExerciseId());
        assertEquals("Test Comment", result.get(0).getComment());
        assertEquals(5, result.get(0).getRating());
        verify(exerciseRepository).findById(1L);
        verify(feedbackRepository).findByExerciseOrderByCreatedAtDesc(testExercise);
    }

    @Test
    void getFeedbacksForExercise_ExerciseNotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> feedbackService.getFeedbacksForExercise(1L));
        verify(exerciseRepository).findById(1L);
        verify(feedbackRepository, never()).findByExerciseOrderByCreatedAtDesc(any());
    }

    @Test
    void deleteFeedback_Success() {
        doNothing().when(feedbackRepository).deleteById(1L);

        feedbackService.deleteFeedback(1L);

        verify(feedbackRepository).deleteById(1L);
    }

    @Test
    void getAllFeedbacks_Success() {
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(testFeedback));

        List<ExerciseFeedbackDto> result = feedbackService.getAllFeedbacks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFeedback.getId(), result.get(0).getId());
        assertEquals(testUser.getId(), result.get(0).getUserId());
        assertEquals(testExercise.getIdExercice(), result.get(0).getExerciseId());
        assertEquals("Test Comment", result.get(0).getComment());
        assertEquals(5, result.get(0).getRating());
        verify(feedbackRepository).findAll();
    }
}