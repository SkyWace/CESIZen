package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.BreathingExerciseHistory;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.BreathingExerciseHistoryRepository;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
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
class BreathingExerciseHistoryServiceTest {

    @Mock
    private BreathingExerciseHistoryRepository historyRepository;

    @Mock
    private BreathingExerciseRepository exerciseRepository;

    @InjectMocks
    private BreathingExerciseHistoryService historyService;

    private User testUser;
    private BreathingExercise testExercise;
    private BreathingExerciseHistory testHistory;

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

        testHistory = new BreathingExerciseHistory();
        testHistory.setId(1L);
        testHistory.setUser(testUser);
        testHistory.setExercise(testExercise);
        testHistory.setCompletedAt(LocalDateTime.now());
    }

    @Test
    void saveHistory_Success() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        when(historyRepository.save(any(BreathingExerciseHistory.class))).thenReturn(testHistory);

        BreathingExerciseHistory result = historyService.saveHistory(testUser, 1L);

        assertNotNull(result);
        assertEquals(testHistory, result);
        verify(exerciseRepository).findById(1L);
        verify(historyRepository).save(any(BreathingExerciseHistory.class));
    }

    @Test
    void saveHistory_ExerciseNotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> historyService.saveHistory(testUser, 1L));
        verify(exerciseRepository).findById(1L);
        verify(historyRepository, never()).save(any(BreathingExerciseHistory.class));
    }

    @Test
    void getHistoryForUser_Success() {
        List<BreathingExerciseHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByUser(testUser)).thenReturn(histories);

        List<BreathingExerciseHistory> result = historyService.getHistoryForUser(testUser);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHistory, result.get(0));
        verify(historyRepository).findByUser(testUser);
    }
}